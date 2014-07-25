package com.ig.igwebcms.servlets;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.polling.importer.ImportException;
import com.day.cq.polling.importer.Importer;
import com.ig.igwebcms.core.model.VedioProperties;

/**
 * The Class YouTubeChannelImporter work as the importer run after 5 min fetch the data from youtube and create the DAM asset which is a image file
 * contains the id of video that required to play the video in iframe.
 */
@Service(value = Importer.class)
@Component
@Property(name = "importer.scheme", value = "uvideo", propertyPrivate = true)
public class YouTubeChannelImporter implements Importer {

    /** The logger. */
    private final static Logger LOGGER = LoggerFactory
            .getLogger(YouTubeChannelImporter.class);

    /**
     * importData method override from importer
     * @param scheme scheme applied at polling config node
     * @param dataSource feedUrl of polling config node like <b>/etc/cloudservices/Youtube/adobe_tv/jcr:content<b> to fetch key and value from confg. page.
     * @param resource target url where to store video node
     */
    @Override
    public void importData(final String scheme, final String dataSource,
                           final Resource resource) throws ImportException {
        try {
            LOGGER.info("dataSource :: " + dataSource);
            Node parent = resource.getResourceResolver()
                    .getResource(dataSource).adaptTo(Node.class);
            LOGGER.info("parent ::: " + parent.getPath());

            String channelId = parent.getProperty("loginUrl").getString();
            String key = parent.getProperty("customerkey").getString();
            LOGGER.info(parent.getProperty("loginUrl").getString() + " ::: "
                    + parent.getProperty("customerkey").getString());
            String pageToken = "";

            callUrl(pageToken, resource, key, channelId);

        }

        catch (Exception e) {
            LOGGER.error("RepositoryException", e);
        }

    }

    /**
     * Call url call the json recursevely until returned json has nextPageToken field. At last page there is no nextPageToken and line
     * <i>json.getString("nextPageToken").toString()<i> will return null and throw the exception after <i>fetchJsonData(json, resource);</i>
     *
     * @param pageToken the page token in case youtube json has more than 50 records
     * @param resource the target where to store node in DAM
     * @param key the Google API Key
     * @param channelId the youtube channel id
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JSONException the JSON exception
     */
    void callUrl(String pageToken, Resource resource, String key,
                 String channelId) throws IOException, JSONException {
        JSONObject json = null;
        if (pageToken != null) {
            String SOURCE_URL = "https://www.googleapis.com/youtube/v3/search?key="
                    + key
                    + "&part=snippet,id&order=date&maxResults=50&type=video&channelId="
                    + channelId + "&pageToken=" + pageToken;
            LOGGER.info("=============SOURCE_URL :: " + SOURCE_URL);
            json = readJsonFromUrl(SOURCE_URL);

            fetchJsonData(json, resource);
            pageToken = json.getString("nextPageToken").toString();
            LOGGER.info("pageToken ::: " + pageToken);

            LOGGER.info("json.toString() ::: " + json.toString());
            callUrl(pageToken, resource, key, channelId);
        }

    }

    /**
     * Parse the json and get the data which will be properties of node.
     *
     * @param json the json Object
     * @param resource the target where to store node in DAM
     * @throws JSONException the JSON exception
     */
    private void fetchJsonData(JSONObject json, Resource resource)
            throws JSONException {
        VedioProperties video = null;
        JSONArray array = json.getJSONArray("items");


        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);

                String videoId = object.getJSONObject("id")
                        .getString("videoId");
                String url = object.getJSONObject("snippet")
                        .getJSONObject("thumbnails").getJSONObject("default")
                        .getString("url");
                String title = object.getJSONObject("snippet").getString(
                        "title");
                String publishDate = object.getJSONObject("snippet").getString(
                        "publishedAt");

                LOGGER.info(videoId + " :: " + url + " :: " + title + " :: "
                        + publishDate);
                video = new VedioProperties(videoId, title, url, publishDate);
                WriteNode(video, resource);
            } catch (Exception e) {
                LOGGER.error("MalformedURLException", e);
            }

        }
    }

    /**
     * Write node to the CRX inside DAM as Video Asset.
     *
     * @param video the videoproperties pojo
     * @param resource the path to store the asset.
     * @throws RepositoryException the repository exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ParseException the parse exception
     */
    private void WriteNode(VedioProperties video, final Resource resource)
            throws RepositoryException, IOException, ParseException {
        Node parent = resource.adaptTo(Node.class);
        LOGGER.info("Parent path ===> " + parent.getPath());

        LOGGER.info("Parent path ===> " + parent.getPath());
        if (parent.hasNode(video.getVideoId())) {
            String date = parent.getNode(video.getVideoId())
                    .getNode("jcr:content").getNode("metadata")
                    .getProperty("publishDate").getString();
            Date publishDate1 = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.ENGLISH).parse(date);
            Date compdate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    .parse(video.getPublishDate());
            LOGGER.info("Existing Date ::: " + publishDate1 + "  New Date ::: "
                    + compdate);
        }
        if (!parent.hasNode(video.getVideoId())) {

            Node videoNode = JcrUtil.createPath(
                    parent.getPath() + "/" + video.getVideoId(), "dam:Asset",
                    parent.getSession());
            Node videoContentNode = JcrUtil.createPath(videoNode.getPath()
                    + "/jcr:content", "dam:AssetContent", parent.getSession());
            Node metadataNode = JcrUtil.createPath(videoContentNode.getPath()
                    + "/metadata", "nt:unstructured", parent.getSession());
            Node renditions = JcrUtil.createPath(videoContentNode.getPath()
                    + "/renditions", "nt:folder", parent.getSession());

            metadataNode.setProperty("dc:format", "video/x-ms-wmv");
            metadataNode.setProperty("dam:size", 26246026);
            metadataNode.setProperty("videoId", video.getVideoId());
            metadataNode.setProperty("thumbnail", video.getThumbnail());
            metadataNode.setProperty("dc:title", video.getTitle());
            metadataNode.setProperty("publishDate", video.getPublishDate());

            parent.getSession().save();

            fileUrl(video.getThumbnail(), video.getVideoId());
            Resource childResource = resource.getChild(video.getVideoId());
            LOGGER.info(childResource.getResourceType());
            Asset asset = com.day.cq.dam.commons.util.DamUtil
                    .resolveToAsset(childResource);

            InputStream stream = new FileInputStream("d:\\cqimages\\"
                    + video.getVideoId() + ".jpeg");
            LOGGER.info("stream ::: " + stream.available());
            asset.addRendition("original", stream, "image/jpeg");
            LOGGER.info("asset.getName() :: " + asset);

        }
    }

    /**
     * Use to store the youtube thumbnail at local to create the renditions in DAM
     *
     * @param fAddress the address of image url
     * @param localFileName the local file name will be id of video
     */
    public static void fileUrl(String fAddress, String localFileName) {
        OutputStream outStream = null;
        URLConnection uCon = null;

        InputStream is = null;
        try {
            URL Url;
            byte[] buf;
            int ByteRead, ByteWritten = 0;
            Url = new URL(fAddress);
            outStream = new BufferedOutputStream(new FileOutputStream(
                    "d:\\cqimages\\" + localFileName + ".jpeg"));

            uCon = Url.openConnection();
            is = uCon.getInputStream();
            buf = new byte[1024];
            while ((ByteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, ByteRead);
                ByteWritten += ByteRead;
            }
            LOGGER.info("Downloaded Successfully.");
            LOGGER.info("File name:\"" + localFileName
                    + "\"\nNo ofbytes :" + ByteWritten);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read json from url.
     *
     * @param url Youtube URL
     * @return the JSON object
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JSONException the JSON exception
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException,
            JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    /**
     * Read all.
     *
     * @param rd the Reader Class
     * @return the String format of json
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
