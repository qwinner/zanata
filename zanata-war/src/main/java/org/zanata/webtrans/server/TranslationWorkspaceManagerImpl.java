package org.zanata.webtrans.server;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.Events;
import org.jboss.seam.web.ServletContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.ZanataInit;
import org.zanata.action.ProjectHome;
import org.zanata.action.ProjectIterationHome;
import org.zanata.common.EntityStatus;
import org.zanata.common.LocaleId;
import org.zanata.dao.AccountDAO;
import org.zanata.model.HIterationProject;
import org.zanata.model.HLocale;
import org.zanata.model.HPerson;
import org.zanata.model.HProject;
import org.zanata.model.HProjectIteration;
import org.zanata.security.ZanataIdentity;
import org.zanata.service.GravatarService;
import org.zanata.webtrans.shared.NoSuchWorkspaceException;
import org.zanata.webtrans.shared.auth.EditorClientId;
import org.zanata.webtrans.shared.model.Person;
import org.zanata.webtrans.shared.model.PersonId;
import org.zanata.webtrans.shared.model.ProjectIterationId;
import org.zanata.webtrans.shared.model.WorkspaceContext;
import org.zanata.webtrans.shared.model.WorkspaceId;
import org.zanata.webtrans.shared.rpc.ExitWorkspace;
import org.zanata.webtrans.shared.rpc.WorkspaceContextUpdate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.ibm.icu.util.ULocale;

@Scope(ScopeType.APPLICATION)
@Name("translationWorkspaceManager")
@Synchronized
public class TranslationWorkspaceManagerImpl implements TranslationWorkspaceManager
{
   private static final Logger LOGGER = LoggerFactory.getLogger(TranslationWorkspaceManagerImpl.class);

   @In
   private AccountDAO accountDAO;

   @In
   GravatarService gravatarServiceImpl;

   private static final String EVENT_WORKSPACE_CREATED = "webtrans.WorkspaceCreated";

   private final ConcurrentHashMap<WorkspaceId, TranslationWorkspace> workspaceMap;
   private final Multimap<ProjectIterationId, TranslationWorkspace> projIterWorkspaceMap;

   public TranslationWorkspaceManagerImpl()
   {
      this.workspaceMap = new ConcurrentHashMap<WorkspaceId, TranslationWorkspace>();
      Multimap<ProjectIterationId, TranslationWorkspace> piwm = HashMultimap.create();
      this.projIterWorkspaceMap = Multimaps.synchronizedMultimap(piwm);
   }

   @Observer(ZanataInit.EVENT_Zanata_Startup)
   public void start()
   {
      LOGGER.info("starting...");
   }

   @Observer(ZanataIdentity.USER_LOGOUT_EVENT)
   public void exitWorkspace(String username)
   {
      String httpSessionId = getSessionId();
      LOGGER.info("User logout: Removing {} from all workspaces, session: {}", username, httpSessionId);
      HPerson person = accountDAO.getByUsername(username).getPerson();
      ImmutableSet<TranslationWorkspace> workspaceSet = ImmutableSet.copyOf(workspaceMap.values());
      for (TranslationWorkspace workspace : workspaceSet)
      {
         Collection<EditorClientId> editorClients = workspace.removeEditorClients(httpSessionId);
         for (EditorClientId editorClientId : editorClients)
         {
            LOGGER.info("Publishing ExitWorkspace event for user {} with editorClientId {} from workspace {}", new Object[] { username, editorClientId, workspace.getWorkspaceContext() });
            // Send GWT Event to client to update the userlist
            ExitWorkspace event = new ExitWorkspace(editorClientId, new Person(new PersonId(username), person.getName(), gravatarServiceImpl.getUserImageUrl(16, person.getEmail())));
            workspace.publish(event);
         }
      }
   }

   private String getSessionId()
   {
      HttpServletRequest request = ServletContexts.instance().getRequest();
      if (request == null)
      {
         return null;
      }
      return request.getSession().getId();
   }

   @Observer(ProjectHome.PROJECT_UPDATE)
   public void projectUpdate(HIterationProject project)
   {
      String projectSlug = project.getSlug();
      LOGGER.info("Project {} updated, status={}", projectSlug, project.getStatus());

      for (HProjectIteration iter : project.getProjectIterations())
      {
         projectIterationUpdate(iter);
      }
   }

   @Observer(ProjectIterationHome.PROJECT_ITERATION_UPDATE)
   public void projectIterationUpdate(HProjectIteration projectIteration)
   {
      String projectSlug = projectIteration.getProject().getSlug();
      String iterSlug = projectIteration.getSlug();
      HProject project = projectIteration.getProject();
      Boolean readOnly = projectIterationIsInactive(project.getStatus(), projectIteration.getStatus());
      LOGGER.info("Project {} iteration {} updated, status={}, readOnly={}", new Object[] {projectSlug, iterSlug, projectIteration.getStatus(), readOnly});

      ProjectIterationId iterId = new ProjectIterationId(projectSlug, iterSlug);
      for (TranslationWorkspace workspace : projIterWorkspaceMap.get(iterId))
      {
         if (readOnly != workspace.getWorkspaceContext().isReadOnly())
         {
            workspace.getWorkspaceContext().setReadOnly(readOnly);
            WorkspaceContextUpdate event = new WorkspaceContextUpdate(readOnly);
            workspace.publish(event);
         }
      }
   }

   private boolean projectIterationIsInactive(EntityStatus projectStatus, EntityStatus iterStatus)
   {
      return !(projectStatus.equals(EntityStatus.ACTIVE) && iterStatus.equals(EntityStatus.ACTIVE));
   }

   private boolean hasNoPermission(HProject project, HLocale locale)
   {
      return !ZanataIdentity.instance().hasPermission("modify-translation", project, locale);
   }

   private boolean isReadOnly(HProject project, EntityStatus iterStatus, HLocale locale)
   {
      // There must be permissions and the project iteration must be in the
      // correct state
      return hasNoPermission(project, locale) || projectIterationIsInactive(project.getStatus(), iterStatus);
   }

   @Destroy
   public void stop()
   {
      LOGGER.info("stopping...");
      LOGGER.info("closing down {} workspaces: ", workspaceMap.size());
   }

   public int getWorkspaceCount()
   {
      return workspaceMap.size();
   }

   @Override
   public TranslationWorkspace getOrRegisterWorkspace(WorkspaceId workspaceId) throws NoSuchWorkspaceException
   {
      TranslationWorkspace workspace = workspaceMap.get(workspaceId);
      if (workspace == null)
      {
         workspace = createWorkspace(workspaceId);
         TranslationWorkspace prev = workspaceMap.putIfAbsent(workspaceId, workspace);

         if (prev == null)
         {
            projIterWorkspaceMap.put(workspaceId.getProjectIterationId(), workspace);
            if (Events.exists())
               Events.instance().raiseEvent(EVENT_WORKSPACE_CREATED, workspaceId);
         }

         return prev == null ? workspace : prev;
      }

      validateAndGetWorkspaceContext(workspaceId);

      return workspace;
   }

   private WorkspaceContext validateAndGetWorkspaceContext(WorkspaceId workspaceId) throws NoSuchWorkspaceException
   {
      Session session = (Session) Component.getInstance("session");

      HProject project = (HProject) session.createQuery("select p from HProject as p where p.slug = :slug").setParameter("slug", workspaceId.getProjectIterationId().getProjectSlug()).uniqueResult();
      if (project.getStatus() == EntityStatus.OBSOLETE)
      {
         throw new NoSuchWorkspaceException("Project is obsolete");
      }

      EntityStatus projectIterationStatus = (EntityStatus) session.createQuery("select it.status from HProjectIteration it where it.slug = :slug and it.project.slug = :pslug").setParameter("slug", workspaceId.getProjectIterationId().getIterationSlug()).setParameter("pslug", workspaceId.getProjectIterationId().getProjectSlug()).uniqueResult();
      if (projectIterationStatus == EntityStatus.OBSOLETE)
      {
         throw new NoSuchWorkspaceException("Project Iteration is obsolete");
      }

      String workspaceName = (String) session.createQuery("select it.project.name || ' (' || it.slug || ')' " + "from HProjectIteration it " + "where it.slug = :slug " + "and it.project.slug = :pslug " + "and it.status <> :status").setParameter("slug", workspaceId.getProjectIterationId().getIterationSlug()).setParameter("pslug", workspaceId.getProjectIterationId().getProjectSlug()).setParameter("status", EntityStatus.OBSOLETE).uniqueResult();
      if (workspaceName == null)
      {
         throw new NoSuchWorkspaceException("Invalid workspace Id");
      }

      HLocale locale = (HLocale) session.createQuery("select l from HLocale l where localeId = :localeId").setParameter("localeId", workspaceId.getLocaleId()).uniqueResult();
      if (locale == null)
      {
         throw new NoSuchWorkspaceException("Invalid Workspace Locale");
      }

      boolean readOnly = isReadOnly(project, projectIterationStatus, locale);
      String localeDisplayName = ULocale.getDisplayName(workspaceId.getLocaleId().toJavaName(), ULocale.ENGLISH);
      return new WorkspaceContext(workspaceId, workspaceName, localeDisplayName, readOnly);
   }

   private TranslationWorkspace createWorkspace(WorkspaceId workspaceId) throws NoSuchWorkspaceException
   {
      WorkspaceContext workspaceContext = validateAndGetWorkspaceContext(workspaceId);
      return new TranslationWorkspaceImpl(workspaceContext);
   }

   public TranslationWorkspace getWorkspace(ProjectIterationId projectIterationId, LocaleId localeId)
   {
      WorkspaceId workspaceId = new WorkspaceId(projectIterationId, localeId);
      return getWorkspace(workspaceId);
   }

   private TranslationWorkspace getWorkspace(WorkspaceId workspaceId)
   {
      return workspaceMap.get(workspaceId);
   }

}