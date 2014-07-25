package com.ig.igwebcms.taglib;

import com.ig.igwebcms.core.helper.LinkGetter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: intelligrape
 * Date: 7/2/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class VersionedClientTag extends BodyTagSupport {

    /*private String category;


    public void setContent(String content) {
        this.content = content;
    }

    private String content;


    public void setCategory(String category) {
        this.category = category;
    }
*/

    public int doAfterBody() throws JspException {
        try {
            BodyContent bc = getBodyContent();
            JspWriter out1 = bc.getEnclosingWriter();


            if (bc != null) {
                LinkGetter linkGetter = new LinkGetter();
                List<String> hrefList = linkGetter.getVersionedLinkTags(pageContext, bc.getString());
                List<String> srcList = linkGetter.getVersionedScriptTags(pageContext, bc.getString());
                System.out.println("seee===========" + hrefList);
                System.out.println("seee===========" + srcList);
                bc.clearBody();
                if (hrefList != null) {
                    Iterator itr = hrefList.iterator();
                    while (itr.hasNext()) {
                        bc.write(itr.next().toString());
                    }
                }
                if (srcList != null) {
                    Iterator itr = srcList.iterator();
                    while (itr.hasNext()) {
                        bc.write(itr.next().toString());
                    }
                }
                setBodyContent(bc);
                out1.print(bc.getString());
            }
        } catch (IOException ioe) {
            throw new JspException("Error: " + ioe.getMessage());
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }


}
