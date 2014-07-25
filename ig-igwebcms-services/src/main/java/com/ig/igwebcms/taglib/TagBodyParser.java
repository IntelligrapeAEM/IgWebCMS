package com.ig.igwebcms.taglib;

import com.ig.igwebcms.core.logging.LoggerUtil;
import com.ig.igwebcms.services.TagBodyParserService;
import org.apache.sling.api.scripting.SlingScriptHelper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * This class parses the tag body for a particular pattern.
 */
public class TagBodyParser extends BodyTagSupport {

    /**
     * This method will be called at the time when
     * tag starts.
     *
     * @return int value that decide what to do after tag start.
     * @throws javax.servlet.jsp.JspException
     */
    @Override
    public final int doStartTag() throws JspException {
        return EVAL_BODY_AGAIN;
    }

    /**
     * This method will be called after tag body is populated now it will
     * have the body text which will be parsed.
     * @return int value that decide what will be the next step.
     */
    @Override
    public final int doAfterBody() {
        BodyContent body = null;
        try {
            SlingScriptHelper sling = (SlingScriptHelper) pageContext.getAttribute("sling");
            TagBodyParserService tagBodyParserService = sling.getService(TagBodyParserService.class);
            body = getBodyContent();
            String bodyText = body.getString();
            JspWriter out = body.getEnclosingWriter();
            bodyText = tagBodyParserService.parseText(bodyText);
            out.println(bodyText);
        } catch (Exception e) {
            LoggerUtil.infoLog(TagBodyParser.class, "Tag body parser serice error - " + e.getMessage());
        }
        return EVAL_PAGE;
    }

    /**
     * This method will be called when Tag body ends.
     * @return int value.
     * @throws JspException
     */
    @Override
    public final int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}
