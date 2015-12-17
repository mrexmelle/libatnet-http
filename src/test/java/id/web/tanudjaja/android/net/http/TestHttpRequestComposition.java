
package id.web.tanudjaja.android.net.http;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.*;

import id.web.tanudjaja.android.common.port.errno;

import static org.junit.Assert.*;

public class TestHttpRequestComposition
{
	@Test
	public void testGetCompositionFromConstructor()
	{
		Map<String, String> m=new LinkedHashMap<>();
		m.put("key", "000maingames000");
		m.put("date", "534044100");		
		HttpGetRequest req=new HttpGetRequest("http://maingames.co.id", m);
		System.out.println("Url: " + req.getUrl());
		assertEquals(req.getUrl(), "http://maingames.co.id?key=000maingames000&date=534044100");
	}

	@Test
	public void testGetCompositionFromEditMapContent()
	{
		HttpGetRequest req=new HttpGetRequest("http://maingames.co.id");
		Map<String, String> m=new LinkedHashMap<>();
		m.put("key", "000maingames000");
		m.put("date", "534044100");
		req.editMapContent(m);
		System.out.println("Url: " + req.getUrl());
		assertEquals(req.getUrl(), "http://maingames.co.id?key=000maingames000&date=534044100");
	}

	@Test
	public void testGetCompositionAfterEditMapContent()
	{
		Map<String, String> m=new LinkedHashMap<>();
		m.put("key", "000maingames000");
		m.put("date", "534044100");		
		HttpGetRequest req=new HttpGetRequest("http://maingames.co.id", m);

		Map<String, String> n=new LinkedHashMap<>();
		n.put("flavor", "release");
		n.put("key", "111maingames111");
		req.editMapContent(n);
		System.out.println("Url: " + req.getUrl());
		assertEquals(req.getUrl(), "http://maingames.co.id?key=111maingames111&date=534044100&flavor=release");
	}

	@Test
	public void testPostContentFromEditMapConstruction()
	{
		Map<String, String> m=new LinkedHashMap<>();
		m.put("key", "000maingames000");
		m.put("date", "534044100");
		HttpPostRequest req=new HttpPostRequest("http://maingames.co.id", m);
		System.out.println("Content: " + new String(req.getContent()));
		assertEquals(new String(req.getContent()), "key=000maingames000&date=534044100");
	}

	@Test
	public void testPostContentFromEditMapContentWhereConstructorTakesMap()
	{
		Map<String, String> m=new LinkedHashMap<>();
		m.put("key", "000maingames000");
		m.put("date", "534044100");
		HttpPostRequest req=new HttpPostRequest("http://maingames.co.id", m);
		Map<String, String> n=new LinkedHashMap<>();
		n.put("flavor", "release");
		n.put("key", "111maingames111");
		int err=req.editMapContent(n);
		assertEquals(errno.SUCCESS, err);
		System.out.println("Content: " + new String(req.getContent()));
		assertEquals(new String(req.getContent()), "key=111maingames111&date=534044100&flavor=release");
	}

	@Test
	public void testPostEditMapContentReturnWhereConstructorTakesBytes()
	{
		HttpPostRequest req=new HttpPostRequest("http://maingames.co.id", "key=000maingames000".getBytes());
		Map<String, String> n=new LinkedHashMap<>();
		n.put("flavor", "release");
		n.put("key", "111maingames111");
		int err=req.editMapContent(n);
		assertEquals(errno.EOPNOTSUPP, err);
	}

	@Test
	public void testPostContentFromEditMapContentWhereConstructorTakesBytes()
	{
		HttpPostRequest req=new HttpPostRequest("http://maingames.co.id", "key=000maingames000".getBytes());
		Map<String, String> n=new LinkedHashMap<>();
		n.put("flavor", "release");
		n.put("key", "111maingames111");
		req.editMapContent(n);
		assertEquals(new String(req.getContent()), "key=000maingames000");
	}
};

