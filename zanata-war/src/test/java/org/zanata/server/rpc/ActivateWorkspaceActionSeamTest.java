package org.zanata.server.rpc;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.dbunit.operation.DatabaseOperation;
import org.testng.annotations.Test;
import org.zanata.ZanataDBUnitSeamTest;
import org.zanata.common.LocaleId;
import org.zanata.webtrans.server.SeamDispatch;
import org.zanata.webtrans.shared.model.ProjectIterationId;
import org.zanata.webtrans.shared.model.WorkspaceId;
import org.zanata.webtrans.shared.rpc.ActivateWorkspaceAction;
import org.zanata.webtrans.shared.rpc.ActivateWorkspaceResult;

@Test(groups = { "seam-tests" })
public class ActivateWorkspaceActionSeamTest extends ZanataDBUnitSeamTest
{

   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
   }

   @Test
   public void activateWorkspaceWithValidData() throws Exception
   {
      new FacesRequest()
      {

         protected void invokeApplication() throws Exception
         {
            SeamDispatch seamDispatch = (SeamDispatch) getInstance(SeamDispatch.class);

            ActivateWorkspaceAction action = new ActivateWorkspaceAction(new WorkspaceId(new ProjectIterationId("sample-project", "1.0"), new LocaleId("en-US")));
            ActivateWorkspaceResult result = seamDispatch.execute(action);

            assertThat(result, notNullValue());
         }
      }.run();
   }

}