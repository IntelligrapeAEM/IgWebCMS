package com.ig.igwebcms.osgi;

import com.ig.igwebcms.core.logging.LoggerUtil;
import com.squeakysand.osgi.framework.BasicBundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Currently This Class is not in use in this project
 * but added for future enhancement.
 */
public class Activator extends BasicBundleActivator {

    /**
     * @param context It is a BundleContext object.
     * @throws java.lang.Exception
     */
    public void start(final BundleContext context) throws Exception {

        LoggerUtil.infoLog(Activator.class, "=-----BUNDLE STARTED----");
    }
}
