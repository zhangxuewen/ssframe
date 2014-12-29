package com.ss.core.io;

import java.io.IOException;
import java.io.InputStream;
/**
 * 
 * @author zhangxuewen
 *
 * @date  下午02:34:02
 */
public interface Resource {
	InputStream getInputStream() throws IOException;
}
