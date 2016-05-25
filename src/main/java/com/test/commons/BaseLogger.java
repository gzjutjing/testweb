package com.test.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by admin on 2016/5/25.
 */
public abstract class BaseLogger {
    Logger logger = LoggerFactory.getLogger(this.getClass());
}
