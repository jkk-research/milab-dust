package hu.sze.milab.dust.net.httpsrv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hu.sze.milab.dust.DustException;
import hu.sze.milab.dust.utils.DustUtils;

public class DustHttpServletDirectFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	File root;
	String pref;
	
	public DustHttpServletDirectFile(String root) {
		this(new File(root));
	}

	public DustHttpServletDirectFile(File root) {
		this.root = root;
		try {
			pref = root.getCanonicalPath();
		} catch (IOException e) {
			DustException.wrap(e, root.getAbsolutePath());
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String path = request.getPathInfo();

		File f = new File(root, path);

		if ( !f.getCanonicalPath().startsWith(pref) ) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if ( !f.isFile() ) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		String ext = DustUtils.getPostfix(path, ".").toLowerCase();
		
		switch ( ext ) {
		case "html":
		case "xhtml":
			response.setContentType("text/html");
			break;
		}

//		response.setContentType("APPLICATION/OCTET-STREAM");
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

		OutputStream out = response.getOutputStream();
		Files.copy(f.toPath(), out);
		out.flush();

	}

}