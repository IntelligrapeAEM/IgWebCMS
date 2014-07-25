package com.ig.igwebcms.taglib;

import com.ig.igwebcms.core.logging.LoggerUtil;
import com.ig.igwebcms.core.model.LinkProperties;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Toolbar extends SimpleTagSupport{

    public void doTag(){

        final Resource resource =(Resource) getJspContext().getAttribute("resource");
        final List linkPropertiesList = new ArrayList();
        try {
            final Iterator itr = (Iterator)resource.listChildren();
            if(itr.hasNext()){
                final Resource subNodeRes =  (Resource)itr.next();
                final Iterator linksItr = (Iterator)subNodeRes.listChildren();
                LinkProperties linkProperties;
                while(linksItr.hasNext())
                {
                    linkProperties=new LinkProperties();
                    final Resource linkRes = (Resource)linksItr.next();
                    final ValueMap linkValMap = linkRes.adaptTo(ValueMap.class);
                    if(linkValMap.get("url")!=null)
                    {
                        linkProperties.setUrl(linkValMap.get("url").toString());
                    }
                    if(linkValMap.get("target")!=null)
                    {
                        linkProperties.setTarget(linkValMap.get("target").toString());
                    }
                    if(linkValMap.get("link_text")!=null)
                    {
                        linkProperties.setLinkText(linkValMap.get("link_text").toString());
                    }
                    linkPropertiesList.add(linkProperties);
                }
            }
            getJspContext().setAttribute("linkProperties" ,linkPropertiesList);
        }catch (Exception e)
        {

            LoggerUtil.errorLog(Toolbar.class,"EXCEPTION--" + e.getMessage() + "Localized Meassage" + e.getLocalizedMessage());
        }
    }
}
