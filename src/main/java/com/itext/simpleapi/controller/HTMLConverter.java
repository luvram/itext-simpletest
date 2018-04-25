package com.itext.simpleapi.controller;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.css.media.MediaDeviceDescription;
import com.itextpdf.html2pdf.css.media.MediaType;
import com.itextpdf.html2pdf.css.util.CssUtils;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

@RestController
public class HTMLConverter {
	public static final String destinationFolder = "target/output/";

	@RequestMapping("/convert")
	public String convertHtml(@RequestParam("url") String url) {
		/** File Name */
		long time = System.currentTimeMillis();
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyymmddhhmmss");
		String str = dayTime.format(new Date(time));
		System.out.println(str);

		/** Destination */
		String destFileName = destinationFolder + str + ".pdf";
		File pdf = new File(destFileName);
		pdf.getParentFile().mkdirs();

		/** Extract Base URL */
		Pattern p = Pattern.compile("((http|https)(:\\/\\/)[A-Za-z0-9-_\\\\.]*|(:(\\d{2,4})))");
		Matcher m = p.matcher(url);
		String baseUri = null;
		if (m.find()) {
			baseUri = m.group();
		}

		System.out.println(baseUri);

		/** Properties */
		float width = CssUtils.parseAbsoluteLength("" + PageSize.A4.rotate().getWidth());

		ConverterProperties converterProperties = new ConverterProperties();

		MediaDeviceDescription mediaDescription = new MediaDeviceDescription(MediaType.SCREEN);
		mediaDescription.setWidth(width);
		converterProperties.setMediaDeviceDescription(mediaDescription);

		converterProperties.setBaseUri(baseUri);

		/** Convert */
		try {
			PdfWriter writer = new PdfWriter(destFileName, new WriterProperties().useDebugMode());
			PdfDocument output = new PdfDocument(writer);
			URLConnection urlConnection = new URL(url).openConnection();
			urlConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
			InputStream input = urlConnection.getInputStream();
			HtmlConverter.convertToPdf(input, output, converterProperties);
		} catch (Exception e) {
			System.out.println("Some error is occured");
		}

		return url;
	}
}
