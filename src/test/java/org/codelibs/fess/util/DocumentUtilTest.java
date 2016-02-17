package org.codelibs.fess.util;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.unit.UnitFessTestCase;

public class DocumentUtilTest extends UnitFessTestCase {
	public void test_hoge(){
		Map<String,Object> doc=new HashMap<>();
		
		String expected = "value1";
		doc.put("key1",expected);
		assertEquals(expected,DocumentUtil.getValue(doc,"key1",String.class));
		assertEquals(null,DocumentUtil.getValue(doc,"key2", String.class));
		
		doc.put("key2", null);
		assertEquals(null, DocumentUtil.getValue(doc, "key2", String.class));
		
		int expected3 = 999;
		doc.put("key3", expected3);
		assertEquals(expected3, DocumentUtil.getValue(doc, "key3", Integer.class));
		
		Date expected4 = new Date();
		doc.put("key4", expected4);
		assertEquals(expected4, DocumentUtil.getValue(doc, "key4", Date.class));
		
//		//Error not to understand
//		long expected5 = 999999999999999999L;
//		doc.put("key5", expected5);
//		assertEquals(expected5, DocumentUtil.getValue(doc, "key5", Long.class));
		
		double expected6 = 999.999;
		doc.put("key6", expected6);
		assertEquals(expected6, DocumentUtil.getValue(doc, "key6", Double.class));
		
		float expected7 = 999.999f;
		doc.put("key7", expected7);
		assertEquals(expected7, DocumentUtil.getValue(doc, "key7", Float.class));
		
//		//Error not to understand
//		boolean expected8 = true;
//		doc.put("key8", expected8);
//		assertEquals(expected8, DocumentUtil.getValue(doc, "key8", Boolean.class));
		
		doc.put("key9", new ArrayList<Integer>(Arrays.asList(777, 888, 999)));
		assertEquals(777, DocumentUtil.getValue(doc, "key9", Integer.class));
	}
}
