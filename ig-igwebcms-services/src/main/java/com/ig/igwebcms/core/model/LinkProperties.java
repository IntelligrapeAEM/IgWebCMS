package com.ig.igwebcms.core.model;

/**
 *  Link properties bean used by {@link com.ig.igwebcms.taglib.Toolbar} for transporting data.
 * variables: url, target, linkText for transferring these information.
 */

public class LinkProperties {

    private String url = "";
    private String target = "";
    private String linkText = "";

    /**
     * Getter method for url
     * @return url
     */
    public final String getUrl() {
        return url;
    }

    /**
     * Setter method for url
     * @param url
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Getter method for target
     * @return target
     */
    public final String getTarget() {
        return target;
    }

    /**
     * Setter method for target
     * @param target
     */
    public void setTarget(final String target) {
        this.target = target;
    }

    /**
     *  Getter method for linkText
     * @return linkText
     */
    public final String getLinkText() {
        return linkText;
    }

    /**
     * Setter method for linkText
     * @param linkText
     */
    public void setLinkText(final String linkText) {
        this.linkText = linkText;
    }
}
