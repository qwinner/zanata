package org.zanata.webtrans.client.view;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import org.zanata.webtrans.client.presenter.SourceContentsPresenter;
import org.zanata.webtrans.client.ui.HasSelectableSource;
import org.zanata.webtrans.shared.model.HasTransUnitId;
import org.zanata.webtrans.shared.model.TransUnit;

import java.util.List;

public interface SourceContentsDisplay extends IsWidget, HasTransUnitId
{
   void setValue(TransUnit value);

   List<HasSelectableSource> getSourcePanelList();

   void setValue(TransUnit value, boolean fireEvents);

   void highlightSearch(String search);

   void setSourceSelectionHandler(ClickHandler clickHandler);


   void refresh();

   void toggleTransUnitDetails(boolean showTransUnitDetails);
}
