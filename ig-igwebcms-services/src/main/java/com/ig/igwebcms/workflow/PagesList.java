package com.ig.igwebcms.workflow;

import com.day.cq.replication.Agent;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.AgentFilter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.ig.igwebcms.core.logging.LoggerUtil;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * This class represent a custom workflow process
 * & responsible for removing pages those have a reference to the
 * modified nodes from dispatcher cache.After this step
 * it will update the node list that there changes has been used
 * in this workflow process.
 */
@Component(enabled = true, immediate = true, metatype = false)
@Service
@Properties({
        @Property(name = "service.description", value = "Used to identify list of pages having a reference to the list of modified nodes under /etc/rates"),
        @Property(name = "service.vendor", value = "Intelligrape"),
        @Property(name = "process.label", value = "List of Pages Referencing Modified Nodes")
})
public class PagesList implements WorkflowProcess {

    /**
     * QueryBuilder Service is used.
     */
    @Reference
    private QueryBuilder builder;

    /**
     * Replicator Service is used.
     */
    @Reference
    private Replicator replicator;

    /**
     * ResourceResolverFactory Service is used.
     */
    @Reference
    private ResourceResolverFactory resourceFactory;

    @Override
    public final void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {

        WorkflowData workflowData = workItem.getWorkflowData();
        HashSet<String> pagesForInvalidation = new HashSet<String>();
        Session session = workflowSession.getSession();
        ArrayList<String> modifiedNodes = (ArrayList<String>) workflowData.getMetaDataMap().get("modifiedNodesList");
        try {
            if (modifiedNodes != null && modifiedNodes.size() > 0) {
                pagesForInvalidation = getPageList(modifiedNodes, session);
                if (pagesForInvalidation.size() > 0) {
                    removeDispatcherCache(pagesForInvalidation, session);
                }
                updateModifiedNodes(modifiedNodes);
            }
        } catch (Exception e) {
            LoggerUtil.errorLog(ModifiedNodesList.class, "Error= " + e);
        }
    }

    /**
     * This method return all the pages list which have
     * a reference to the modified nodes list provide from last
     * workflow process {@link ModifiedNodesList}.
     *
     * @param modifiedNodes This the list of Modified nodes under page
     *                      jcr:content node and provided by last workflow process.
     * @param session       It is WorkFlowSession object.
     * @return HashSet i.e. the list of pages which have a reference
     *         to the modified nodes.
     * @throws Exception
     */
    private HashSet<String> getPageList(final ArrayList<String> modifiedNodes, final Session session) throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        HashSet<String> pagesForInvalidation = new HashSet<String>();
        Query query = null;
        SearchResult result = null;
        String tempPagePath = "";
        int tempIndex = 0;
        map.put("type", "nt:unstructured");
        map.put("property", "sling:resourceType");
        map.put("property.value", "ig-igwebcms/components/content/text");
        for (String nodePath : modifiedNodes) {
            map.put("fulltext", "%" + nodePath + "%");
            query = builder.createQuery(PredicateGroup.create(map), session);
            result = query.getResult();
            for (Hit hit : result.getHits()) {
                tempPagePath = hit.getPath();
                tempIndex = tempPagePath.lastIndexOf("/jcr:content");
                if (tempIndex >= 0) {
                    tempPagePath = tempPagePath.substring(0, tempIndex);
                }
                pagesForInvalidation.add(tempPagePath);
            }
        }
        return pagesForInvalidation;
    }

    /**
     * This method is used to remove the list of pages which have a reference
     * to the modified node from dispatcher cache.
     *
     * @param pagesForInvalidation Unique List of modified pages.
     * @param session              WorkFlowSession object.
     * @throws Exception
     */
    private void removeDispatcherCache(final HashSet<String> pagesForInvalidation, final Session session) throws Exception {

        ReplicationOptions opts = new ReplicationOptions();
        opts.setFilter(new AgentFilter() {
            public boolean isIncluded(final Agent agent) {
                return ("flush".equals(agent.getConfiguration().getSerializationType()) && agent.isEnabled());
            }
        });
        for (String pagePath : pagesForInvalidation) {
            replicator.replicate(session, ReplicationActionType.ACTIVATE, pagePath, opts);
        }
    }

    /**
     * This is the last step where we update the
     * modified node flag so that it is confirmed that the
     * new node values become used.
     *
     * @param modifiedNodes List of node which has been modified.
     */
    private void updateModifiedNodes(final ArrayList<String> modifiedNodes) {
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceFactory.getAdministrativeResourceResolver(null);
            Resource resource = null;
            Node node = null;
            for (String nodePath : modifiedNodes) {
                LoggerUtil.infoLog(PagesList.class, "===== ******* ======" + nodePath);
                resource = resourceResolver.getResource(nodePath);
                if (!ResourceUtil.isNonExistingResource(resource)) {
                    node = resource.adaptTo(Node.class);
                    node.setProperty("modified", "N");
                }
            }
            resourceResolver.commit();
        } catch (Exception e) {
            LoggerUtil.errorLog(ModifiedNodesList.class, "Exception = " + e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }
}
