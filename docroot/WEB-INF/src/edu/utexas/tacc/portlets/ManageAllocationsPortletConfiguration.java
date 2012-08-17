/**
 * 
 */
package edu.utexas.tacc.portlets;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.portlet.BaseConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.PortletPreferencesFactoryUtil;

/**
 * @author mrhanlon
 *
 */
public class ManageAllocationsPortletConfiguration extends BaseConfigurationAction {

	@Override
	public void processAction(PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {
		String name = actionRequest.getParameter("mode");
		PortletPreferences preferences = actionRequest.getPreferences();

		String portletResource = ParamUtil.getString(actionRequest, "portletResource");

		if (Validator.isNotNull(portletResource)) {
			preferences = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest, portletResource);
		}
		
		preferences.setValue("mode", name);
		preferences.store();
		
		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	public String render(PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
			throws Exception {
		return "/jsp/config.jsp";
	}

}
