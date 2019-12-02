package com.lind.common.util;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 配置文件管理器.
 */
public class FilterConfigManager {
  private static Document document = null;
  private static FilterConfigManager filterConfigManager = null;

  private FilterConfigManager() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      document = db.parse(FilterConfigManager.class.getResourceAsStream("/commonFilterConfig.xml"));
    } catch (Exception var3) {
      var3.printStackTrace();
    }

  }

  static FilterConfigManager getFilterConfigManagerInstance() {
    if (filterConfigManager == null) {
      filterConfigManager = new FilterConfigManager();
    }

    return filterConfigManager;
  }

  public static String getItemValue(String textlabel) {
    getFilterConfigManagerInstance();
    NodeList list = document.getElementsByTagName(textlabel);
    String result = "";
    if (list != null && list.getLength() > 0) {
      for (int i = 0; i < list.getLength(); ++i) {
        Element element = (Element) list.item(i);
        result = result + element.getTextContent() + ",";
      }

      result = result.substring(0, result.length() - 1);
    }

    return result;
  }
}
