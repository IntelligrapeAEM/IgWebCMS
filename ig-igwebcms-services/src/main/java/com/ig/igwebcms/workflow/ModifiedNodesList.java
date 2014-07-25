package com.ig.igwebcms.workflow;

import com.day.cq.wcm.api.Page;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class finds all the modified child nodes list under
 * page jcr:content node & provides that list to the next step
 * in workflow.
 */
@Component(enabled = true, immediate = true, metatype = false)
@Service
@Properties({
        @Property(name = "service.description", value = "used to find all the node modified under /etc/rates directory"),
        @Property(name = "service.vendor", value = "Intelligrape"),
        @Property(name = "process.label", value = "Modified Nodes List")
})
public class ModifiedNodesList implements WorkflowProcess {

    /**
     * ResourceResolverFactory Service is used.
     */
    @Reference
    private ResourceResolverFactory resourceFactory;

    /**
     * This is the default method of WorkflowProcess Interface &
     * it is responsible to providing modified child nodes
     * under the current page to the next step of workflow.
     *
     * @param workItem        WorkItem class object.
     * @param workflowSession WorkflowSession class object.
     * @param metaDataMap     MetaDataMap class object.
     * @throws WorkflowException
     */
    @Override
    public final void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {

        WorkflowData data = workItem.getWorkflowData();
        String path = null;
        path = data.getPayload().toString();
        ResourceResolver resourceResolver = null;
        ArrayList<String> modifiedNodesList = new ArrayList<String>();
        String parentResPath = "";
        try {
            resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
            parentResPath = path.substring(0, path.indexOf("/jcr:content"));
            Resource resource = resourceResolver.getResource(parentResPath);
            Page page = resource.adaptTo(Page.class);
            if (page != null) {
                resource = resourceResolver.getResource(path);
                Iterator<Resource> resourceList = resource.listChildren();
                Resource tempResource = null;
                String isModified = "N";
                while (resourceList.hasNext()) {
                    tempResource = resourceList.next();
                    ValueMap valueMap = ResourceUtil.getValueMap(tempResource);
                    isModified = valueMap.get("modified", "N");
                    if (isModified.equals("Y")) {
                        modifiedNodesList.add(valueMap.get("location", ""));
                    }
                }
                data.getMetaDataMap().put("modifiedNodesList", modifiedNodesList);
            }
        } catch (Exception e) {
            LoggerUtil.errorLog(ModifiedNodesList.class, "Exception = " + e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }
}
