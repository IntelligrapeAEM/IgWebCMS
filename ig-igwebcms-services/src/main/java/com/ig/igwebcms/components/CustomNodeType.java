package com.ig.igwebcms.components;

/**
 * This class is to register custom node types.
 */

import com.ig.igwebcms.core.logging.LoggerUtil;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;

import javax.jcr.NamespaceRegistry;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component(label = "custom node type definition", description = "this component will register the custom node type",
        enabled = true, metatype = true, immediate = true)
public class CustomNodeType {

    @Reference
    SlingRepository slingRepository;

    /**
     * Activate method for CustomNodeType component
     * @param componentContext
     */
    @Activate
    public void activate(ComponentContext componentContext) {
        LoggerUtil.infoLog(CustomNodeType.class, "inside activate method");
        registerCustomNodeUsingCnd();
        registerCustomNodeTypes();
        LoggerUtil.infoLog(CustomNodeType.class,"activate method completed");
    }

    /**
     * This method creates custom node type programmatically without using CND files.
     */
    private void registerCustomNodeTypes() {

        LoggerUtil.infoLog(CustomNodeType.class,"inside registerCustomNodeTypes() method");
        Session session = null;
        try {
            session = slingRepository.loginAdministrative(null);
            LoggerUtil.infoLog(CustomNodeType.class,"session is" + session);
            NodeTypeManager manager = (NodeTypeManager)
                    session.getWorkspace().getNodeTypeManager();
            NamespaceRegistry ns=session.getWorkspace().getNamespaceRegistry();
            ns.registerNamespace("mjs","http://www.intelligrape.com/mjCustomNodeT");
            LoggerUtil.infoLog(CustomNodeType.class,"namespac registered" );
            NodeTypeTemplate nodeTypeTemplate = manager.createNodeTypeTemplate();
            nodeTypeTemplate.setName("mjs:testNodeType");
            LoggerUtil.infoLog(CustomNodeType.class,"NodeTypeManagr template is" + nodeTypeTemplate);
            PropertyDefinitionTemplate customProperty = manager.createPropertyDefinitionTemplate();
            customProperty.setName("mjs:Name");
            customProperty.setRequiredType(PropertyType.STRING);
            LoggerUtil.infoLog(CustomNodeType.class,"property definition template is" + customProperty);

            PropertyDefinitionTemplate customProperty1 = manager.createPropertyDefinitionTemplate();
            customProperty1.setName("mjs:City");
            customProperty1.setRequiredType(PropertyType.STRING);
            LoggerUtil.infoLog(CustomNodeType.class,"property definition template is" + customProperty1);

            nodeTypeTemplate.getPropertyDefinitionTemplates().add(customProperty);
            nodeTypeTemplate.getPropertyDefinitionTemplates().add(customProperty1);

            LoggerUtil.infoLog(CustomNodeType.class,"nodetypetemplate after updation  is" + nodeTypeTemplate);
            manager.registerNodeType(nodeTypeTemplate, true);
            session.save();
            LoggerUtil.infoLog(CustomNodeType.class,"session saved");
        } catch (RepositoryException e) {
            LoggerUtil.infoLog(CustomNodeType.class,"exception occured");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            LoggerUtil.infoLog(CustomNodeType.class,"inside finally");
            session.logout();
            LoggerUtil.infoLog(CustomNodeType.class,"finally completed");
        }
    }

    /**
     *  This method creates custom node type programmatically without using CND files.
     */
    public void registerCustomNodeUsingCnd() {

        LoggerUtil.infoLog(CustomNodeType.class,"inside registerCustomNodeUsingCnd() method");
        Session session = null;
        try {
            session = slingRepository.loginAdministrative(null);
            LoggerUtil.infoLog(CustomNodeType.class,"session is" + session);

                NodeTypeManager manager = (NodeTypeManager)
                    session.getWorkspace().getNodeTypeManager();
            if (!manager.hasNodeType("igwebTestNodeType:testNodeType")) {
                InputStream fis = CustomNodeType.class.getResourceAsStream("/custom/igwebcms.cnd");
                LoggerUtil.infoLog(CustomNodeType.class,"is found" + fis);
                NodeType[] nodeTypes = CndImporter.registerNodeTypes(new InputStreamReader(fis), session);
                for (NodeType nt : nodeTypes) {
                    LoggerUtil.infoLog(CustomNodeType.class,"Registered: " + nt.getName());
                }
                session.save();
                LoggerUtil.infoLog(CustomNodeType.class,"session saved");
            } else {
                LoggerUtil.infoLog(CustomNodeType.class,"NodeType already registered");
            }
        } catch (Exception e) {
            LoggerUtil.infoLog(CustomNodeType.class,"exception occured");
            LoggerUtil.infoLog(CustomNodeType.class,e.getMessage());
        } finally {
            LoggerUtil.infoLog(CustomNodeType.class,"inside finally");
            session.logout();
            LoggerUtil.infoLog(CustomNodeType.class,"finally completed");
        }


    }

}
