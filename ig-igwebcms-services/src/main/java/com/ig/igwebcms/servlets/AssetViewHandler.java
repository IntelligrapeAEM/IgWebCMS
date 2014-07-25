package com.ig.igwebcms.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.core.contentfinder.Hit;
import com.day.cq.wcm.core.contentfinder.ViewHandler;
import com.day.cq.wcm.core.contentfinder.ViewQuery;
import com.day.cq.xss.ProtectionContext;
import com.day.cq.xss.XSSProtectionException;
import com.day.cq.xss.XSSProtectionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.query.GQL;
import org.apache.jackrabbit.commons.query.GQL.Filter;
import org.apache.jackrabbit.commons.query.GQL.ParserCallback;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype=false, label="assetviewhandler", description="")
@Service
@Properties({@org.apache.felix.scr.annotations.Property(name="sling.servlet.paths", value={"/bin/wcm/contentfinder/asset/viewnew"}, propertyPrivate=true)})
public class AssetViewHandler
        extends ViewHandler
{
    private static final long serialVersionUID = 5539993103124811086L;
    @Reference(policy=ReferencePolicy.STATIC)
    private XSSProtectionService xss;
    //private static Logger LOGGER = LoggerFactory.getLogger(AssetViewHandler.class);
    public static final String DEFAULT_START_PATH = "/content/dam/youtube-videos";
    public static final String DEFAULT_MIME_TYPE = "defaultMimeType";
    public static final String MIME_TYPE = "mimeType";
    private static final String METADATA_DC_FORMAT = "metadata/dc:format";
    private static final String ORIG_CONTENT = "jcr:content/renditions/original/jcr:content";
    private static final String MIME_REL_PATH = "jcr:content/metadata/dc:format";
    private static final String LAST_MOD_REL_PATH = "jcr:content/jcr:lastModified";
    private final static Logger LOGGER = LoggerFactory.getLogger(AssetViewHandler.class);
    /**
     * This function is customized from OOTB AssetViewHandler.
     */
    protected ViewQuery createQuery(SlingHttpServletRequest request, Session session, String queryString)
            throws RepositoryException
    {
	  /*
	   * If page doesn't tagged with specific youtube cloud service then adobe tv video will get display in CF.
	   */
        String cloudService="/etc/cloudservices/Youtube/adobe_tv";
        String target="/content/dam/youtube-videos/adobe_tv";
        ParserCallback cb = new ParserCallback((TagManager)request.getResourceResolver().adaptTo(TagManager.class));
        GQL.parse(queryString, session, cb);
        StringBuilder gql = cb.getQuery();
    /*
     * url from CF's JS will be in format like #/content/Geomatrixx/en/home.html
     */
        String url =	request.getParameter("url");
        String urlActual=url.replace("#", "").replace(".html", "");

        LOGGER.info("URL is ::: "+urlActual);
    /*
     * From the Page url, fetch the cloud services.
     */
        Node pageJcrContent=request.getResourceResolver().getResource(urlActual+"/jcr:content/").adaptTo(Node.class);
        if(pageJcrContent.hasProperty("cq:cloudserviceconfigs"))
        {
            Property props= pageJcrContent.getProperty("cq:cloudserviceconfigs");
            LOGGER.info("isMultiple ::: "+props.isMultiple());
            if(props.isMultiple())
            {
                LOGGER.info("Inside IF");
                Value values[]=props.getValues();
                LOGGER.info("values ::: "+values.length);
                for(Value val:values)
                {
                    if(val.getString().contains("/Youtube_Connect/"))
                    {
                        cloudService=val.getString();
                    }

                }
            }
            else
            {   LOGGER.info("Inside Else ::::: ");
                String cloudser=props.getValue().getString();

                if(cloudser.contains("/Youtube_Connect/"))
                {
                    cloudService=cloudser;
                }
            }
            LOGGER.info("cloudService ::: "+cloudService);
    /*
     * from the cloudservice will go to the polling config node to get the target./etc/cloudservices/Youtube/adobe_tv
     * So Configuration page should be of same name as polling config node.
     */
            Node cloudServiceNode=request.getResourceResolver().getResource(cloudService+"/jcr:content/youtubepolling").adaptTo(Node.class);
            target=cloudServiceNode.getProperty("target").getString();
            LOGGER.info("target ::: "+target);
        }
        String path = cb.getStartPath();
        // path="/content/dam/youtube-videos/intelligrape";
        if (path == null)
        {
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            // String startPath = pathInfo.getSuffix() != null ? pathInfo.getSuffix() : "/content/dam/youtube-videos/adobe_tv";
            LOGGER.info("target inside page=null ::: "+target);

            String startPath = pathInfo.getSuffix() != null ? pathInfo.getSuffix() : target;

            LOGGER.info("startPath ::: "+startPath);


            path = "path:\"" + startPath + "\"";
            LOGGER.info("path ::: "+path);
        }
        if (gql.length() != 0) {
            gql.append(" ");
        }
        gql.append(path).append(" ");


        String limit = cb.getLimit();
        if (limit == null)
        {
            limit = request.getParameter("limit");
            if ((limit != null) && (!limit.equals(""))) {
                limit = "limit:" + limit;
            } else {
                limit = "limit:20";
            }
        }
        gql.append(limit).append(" ");
        if (((queryString == null) || (queryString.length() == 0)) && (request.getParameter("type") == null)) {
            return new MostRecentAssets(request, session, gql, this.xss);
        }
        String type = cb.getType();
        if (type == null)
        {
            type = request.getParameter("type");
            if (type == null) {
                type = "dam:Asset";
            }
            type = "type:\"" + type + "\"";
        }
        gql.append(type).append(" ");


        String order = cb.getOrder();
        if (order == null) {
            order = "order:-jcr:content/jcr:lastModified";
        }
        gql.append(order).append(" ");


        gql.append(getMimeTypeConstraint(request, "jcr:content/metadata/dc:format"));

        return new GQLViewQuery(gql.toString(), request.getResourceResolver(), this.xss);
    }

    private static CharSequence getMimeTypeConstraint(SlingHttpServletRequest request, String relPath)
    {
        StringBuilder constraint = new StringBuilder();
        String mimeType = request.getParameter("mimeType");
        if (mimeType == null) {
            mimeType = request.getParameter("defaultMimeType");
        }
        if (mimeType != null)
        {
            String[] values = mimeType.split(",");
            String or = "";
            for (String v : values)
            {
                constraint.append(or);
                or = "OR ";
                constraint.append("\"").append(relPath).append("\":");
                constraint.append("\"").append(v).append("\" ");
            }
        }
        return constraint;
    }

    private static Hit createHit(Asset asset, String excerpt, XSSProtectionService xss)
            throws RepositoryException
    {
        Hit hit = new Hit();
        hit.set("name", asset.getName());
        hit.set("path", asset.getPath());
        Node assetNode1 = asset.adaptTo(Node.class);
        LOGGER.info("assetNode.getPath() ::: "+assetNode1.getPath());
        String title=assetNode1.getNode("jcr:content/metadata").getProperty("dc:title").getString();
        hit.set("title", title);
        LOGGER.info("asset.getPath() ::: "+asset.getPath()+" ::: title ::: "+title);
        if (xss != null) {
            try
            {
                hit.set("title_xss", xss.protectForContext(ProtectionContext.PLAIN_HTML_CONTENT, title));
            }
            catch (XSSProtectionException e)
            {
                LOGGER.warn("Unable to protect title {}", title);
            }
        }
        hit.set("excerpt", excerpt);

        Node assetNode = (Node)asset.adaptTo(Node.class);
        if (assetNode.hasProperty("jcr:content/metadata/dc:format")) {
            hit.set("mimeType", assetNode.getProperty("jcr:content/metadata/dc:format").getString());
        }
        hit.set("lastModified", Long.valueOf(asset.getLastModified()));

        long ck = 0L;
        try
        {
            Node n = (Node)asset.getRendition("cq5dam.thumbnail.48.48.png").adaptTo(Node.class);
            ck = n.getNode("jcr:content").getProperty("jcr:lastModified").getLong();

            ck = ck / 1000L * 1000L;
        }
        catch (Exception e) {}
        hit.set("ck", Long.valueOf(ck));

        long size = 0L;
        Rendition currentOriginal = asset.getOriginal();
        if (currentOriginal != null) {
            size = currentOriginal.getSize();
        }
        // hit.set("size", Long.valueOf(size));
        hit.set("size", "");
        return hit;
    }

    protected void bindXss(XSSProtectionService paramXSSProtectionService)
    {
        this.xss = paramXSSProtectionService;
    }

    protected void unbindXss(XSSProtectionService paramXSSProtectionService)
    {
        if (this.xss == paramXSSProtectionService) {
            this.xss = null;
        }
    }

    private class ParserCallback
            implements GQL.ParserCallback
    {
        private StringBuilder query = new StringBuilder();
        private String startPath = null;
        private String type = null;
        private String limit = null;
        private String order = null;
        private TagManager tagMgr;

        public ParserCallback(TagManager tagMgr)
        {
            this.tagMgr = tagMgr;
        }

        public void term(String property, String value, boolean optional)
                throws RepositoryException
        {
            if (property.equals("path"))
            {
                this.startPath = ("path:\"" + value + "\"");
                return;
            }
            if (property.equals("type"))
            {
                this.type = ("type:\"" + value + "\"");
                return;
            }
            if (property.equals("limit"))
            {
                this.limit = ("limit:\"" + value + "\"");
                return;
            }
            if (property.equals("order"))
            {
                this.order = ("order:\"" + value + "\"");
                return;
            }
            if (property.equals("name"))
            {
                this.order = ("name:\"" + value + "\"");
                return;
            }
            if (optional) {
                this.query.append("OR ");
            }
            if (property.equals("tag"))
            {
                if (this.tagMgr == null)
                {
                    this.query.append("\"jcr:content/metadata/cq:tags\":\"");
                    this.query.append(value).append("\" ");
                }
                else
                {
                    Tag tag = this.tagMgr.resolve(value);
                    if (tag != null)
                    {
                        this.query.append(tag.getGQLSearchExpression("jcr:content/metadata/cq:tags"));
                        this.query.append(" ");
                    }
                    else
                    {
                        this.query.append("\"jcr:content/metadata/cq:tags\":\"______invalid______\" ");
                    }
                }
            }
            else if (property.length() == 0)
            {
                this.query.append("\"").append(value).append("\" OR ");

                this.query.append("\"jcr:content/metadata/.\":\"");
                this.query.append(value).append("\" OR ");

                this.query.append("\"jcr:content/renditions/original/jcr:content/.\":\"");
                this.query.append(value).append("\" ");
            }
            else
            {
                property = "jcr:content/metadata/" + property;
                this.query.append("\"").append(property).append("\":");
                this.query.append("\"").append(value).append("\" ");
            }
        }

        public StringBuilder getQuery()
        {
            return this.query;
        }

        public String getStartPath()
        {
            return this.startPath;
        }

        public String getType()
        {
            return this.type;
        }

        public String getLimit()
        {
            return this.limit;
        }

        public String getOrder()
        {
            return this.order;
        }
    }

    private static class MostRecentAssets
            implements ViewQuery
    {
        private final SlingHttpServletRequest request;
        private final Session session;
        private final String gql;
        private final XSSProtectionService xss;

        public MostRecentAssets(SlingHttpServletRequest request, Session session, StringBuilder gql, XSSProtectionService xss)
        {

            this.request = request;
            this.session = session;
            gql.append("type:\"").append("dam:AssetContent").append("\" ");
            gql.append("order:-").append("jcr:lastModified").append(" ");
            gql.append(AssetViewHandler.getMimeTypeConstraint(request, "metadata/dc:format"));
            this.gql = gql.toString();
            this.xss = xss;
        }

        public Collection<Hit> execute()
        {
            List<Hit> hits = new ArrayList();
            ResourceResolver resolver = this.request.getResourceResolver();

            AssetViewHandler.SubAssetFilter filter = new AssetViewHandler.SubAssetFilter(2);

            AssetViewHandler.LOGGER.debug("executing GQL query: " + this.gql);

            RowIterator rows = GQL.execute(this.gql, this.session, null, filter);
            try
            {
                while (rows.hasNext())
                {
                    Row row = rows.nextRow();
                    String path = row.getValue("jcr:path").getString();
                    path = Text.getRelativeParent(path, 1);
                    Asset asset = (Asset)resolver.getResource(path).adaptTo(Asset.class);
                    if (asset != null)
                    {
                        String excerpt;
                        try
                        {
                            excerpt = row.getValue("rep:excerpt()").getString();
                        }
                        catch (Exception e)
                        {
                            excerpt = "";
                        }
                        hits.add(AssetViewHandler.createHit(asset, excerpt, this.xss));
                    }
                }
            }
            catch (RepositoryException re)
            {
                AssetViewHandler.LOGGER.error("A repository error occurred", re);
            }
            return hits;
        }
    }

    private static class GQLViewQuery
            implements ViewQuery
    {
        String queryStr;
        Session session;
        ResourceResolver resolver;
        XSSProtectionService xss;

        public GQLViewQuery(String queryStr, ResourceResolver resolver, XSSProtectionService xss)
        {
            this.queryStr = queryStr;
            this.session = ((Session)resolver.adaptTo(Session.class));
            this.resolver = resolver;
            this.xss = xss;
        }

        public Collection<Hit> execute()
        {
            List<Hit> hits = new ArrayList();

            AssetViewHandler.SubAssetFilter filter = new AssetViewHandler.SubAssetFilter(1);

            AssetViewHandler.LOGGER.debug("executing GQL query: " + this.queryStr);
            RowIterator rows = GQL.execute(this.queryStr, this.session, null, filter);
            try
            {
                while (rows.hasNext())
                {
                    Row row = rows.nextRow();
                    String path = row.getValue("jcr:path").getString();
                    Asset asset = (Asset)this.resolver.getResource(path).adaptTo(Asset.class);
                    if (asset != null)
                    {
                        String excerpt;
                        try
                        {
                            excerpt = row.getValue("rep:excerpt()").getString();
                        }
                        catch (Exception e)
                        {
                            excerpt = "";
                        }
                        hits.add(AssetViewHandler.createHit(asset, excerpt, this.xss));
                    }
                }
            }
            catch (RepositoryException re)
            {
                LOGGER.error("A repository error occurred", re);
            }
            return hits;
        }
    }

    private static class SubAssetFilter
            implements GQL.Filter
    {
        private final int level;

        public SubAssetFilter(int level)
        {
            this.level = level;
        }

        public boolean include(Row row)
                throws RepositoryException
        {
            String path = row.getValue("jcr:path").getString();
            String name = Text.getName(Text.getRelativeParent(path, this.level));
            return !name.equals("subassets");
        }
    }
}
