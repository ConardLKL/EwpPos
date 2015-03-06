package com.limahao.ticket.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.limahao.ticket.log.DebugLog;

/**
 * @author shou_peng
 * @version 创建时间：2012-9-4 下午12:18:00
 * 类说明
 */
public class MyXml {
	boolean 	m_bLoadError;
	Document 	m_Document;
	Element 	m_DocRoot;
	NodeList 	m_NodeList;
	Node 		m_Node;
	private final static String TAG = "MyXml";
	int 		m_nNodeIndex = 0;

	public boolean loadXML(byte[] sXML)
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {

			docBuilder = docFactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(sXML);
			
			m_Document = docBuilder.parse(is,"GB2312");
			m_DocRoot = m_Document.getDocumentElement();
			//XPathFactory factory = XPathFactory.newInstance();
			//m_xPath = factory.newXPath();
		} catch (Exception e) {
			e.printStackTrace();
			return false;			
		} 
		return true;
	}
	
	public boolean loadXML(byte[] sXML, String sCodec)
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			String str = new String(sXML, sCodec);
			docBuilder = docFactory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(str.getBytes());
			
			m_Document = docBuilder.parse(is);
			m_DocRoot = m_Document.getDocumentElement();
			//XPathFactory factory = XPathFactory.newInstance();
			//m_xPath = factory.newXPath();
		} catch (Exception e) {
			e.printStackTrace();
			return false;			
		} 
		return true;
	}
	
	public boolean loadIS(InputStream is, String sCodec)
	{

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			m_Document = docBuilder.parse(is);
			m_DocRoot = m_Document.getDocumentElement();
			//XPathFactory factory = XPathFactory.newInstance();
			//m_xPath = factory.newXPath();
		} catch (Exception e) {
			e.printStackTrace();
			return false;			
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;			
		}
		return true;
	}
	
	public boolean SelectNodeToList(String sExpress)
	{
		if(m_DocRoot==null)
			return false;
		m_NodeList = GetNodeList(sExpress);
		m_nNodeIndex = 0;
		if(m_NodeList !=null)
		{
			return true;
		}
		else
			return false;
	}
	
	public NodeList GetNodeList(String sExpress)
	{
		if(m_DocRoot==null)
			return null;
		try {
			String [] sNodePaths = sExpress.split("/");
			
			Element currentNode = m_DocRoot;
			List<String> sNodeTree = new ArrayList<String>();
			for(int i = 0;i<sNodePaths.length;i++)
			{
				if(sNodePaths[i].length()!=0)
				{
					sNodeTree.add(sNodePaths[i]);
				}
			}
			for(int i = 0;i<sNodeTree.size() - 1;i++)
			{
//				DebugLog.i(i+": ");
//				DebugLog.i(sNodeTree.get(i));
				if(!sNodeTree.get(i).equals(""))
				{
//					DebugLog.i("currentNode = (Element) (currentNode.getElementsByTagName(sNodeTree.get(i))).item(0);");
					currentNode = (Element) (currentNode.getElementsByTagName(sNodeTree.get(i))).item(0);
				}
			}
//			DebugLog.i(sNodeTree.size()-1+": ");
//			DebugLog.i(sNodeTree.get(sNodeTree.size()-1));
			try {
				m_NodeList = currentNode.getElementsByTagName(sNodeTree.get(sNodeTree.size()-1));
				if(m_NodeList.getLength() == 0 && currentNode.getNodeName().equals(sNodeTree.get(sNodeTree.size()-1)) &&currentNode == m_DocRoot)
				{
					m_NodeList = m_Document.getChildNodes();
				}//修正Android4.0系统收不到消息的bug
			} catch (Exception e) {
				DebugLog.e(TAG, "GetNodeList m_NodeList = currentNode.getElementsByTagName(sNodeTree.get(sNodeTree.size()-1)) error.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(m_NodeList !=null)
		{
			return m_NodeList;
		}
		else
			return null;
	}
	
	public Node GetNode(String sExpress)
	{
		if(m_DocRoot==null)
			return null;
		if(sExpress.equals("."))
			return m_DocRoot;
	
		try {

			String [] sNodePaths = sExpress.split("/");
			
			Element currentNode = m_DocRoot;
			List<String> sNodeTree = new ArrayList<String>();
			for(int i = 0;i<sNodePaths.length;i++)
			{
				if(sNodePaths[i].length()!=0)
				{
					sNodeTree.add(sNodePaths[i]);
				}
			}
			for(int i = 0;i<sNodeTree.size() - 1;i++)
			{
//				DebugLog.i(i+": ");
//				DebugLog.i(sNodeTree.get(i));
				if(!sNodeTree.get(i).equals(""))
				{
//					DebugLog.i("currentNode = (Element) (currentNode.getElementsByTagName(sNodeTree.get(i))).item(0);");
					currentNode = (Element) (currentNode.getElementsByTagName(sNodeTree.get(i))).item(0);
				}
			}
//			DebugLog.i(sNodeTree.size()-1+": ");
//			DebugLog.i(sNodeTree.get(sNodeTree.size()-1));
			
			m_NodeList = currentNode.getElementsByTagName(sNodeTree.get(sNodeTree.size()-1));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(m_NodeList !=null)
		{
			return m_NodeList.item(0);
		}
		else
			return null;
	}
	
	public Node QueryNode(boolean bReset)
	{
//		DebugLog.i("QueryNode "+bReset);
		if (bReset)
		{
			m_nNodeIndex = 0;
			return m_NodeList.item(m_nNodeIndex);
		}
		
		try
		{
//			DebugLog.i("QueryNode "+bReset);
			m_Node = null;
			m_Node = m_NodeList.item(m_nNodeIndex);
			m_nNodeIndex++;
//			DebugLog.i("m_nNodeIndex"+m_nNodeIndex+"="+m_Node);	
		}
		catch(Exception e)
		{
			DebugLog.w(TAG, "QueryNode m_Node = m_NodeList.item(m_nNodeIndex) error.");
		}
		
		return m_Node;
	}

	public String GetValueByName(String sName)
	{
		//DebugLog.i(TAG, "GetValueByName: sName = "+sName);
		Element element = (Element)m_Node;
		String sValue = null;
		if(sName.equals("."))
		{
			try{
				sValue = m_Node.getFirstChild().getNodeValue();
			}catch(Exception e){
				DebugLog.e(TAG, "GetValueByName("+ sName +") --> sValue = m_Node.getFirstChild().getNodeValue() error.");
			}	
		}
		else
		{
			try
			{
				NodeList nodelist = element.getElementsByTagName(sName);
				if(nodelist != null)
				{
					sValue = nodelist.item(0).getFirstChild().getNodeValue();
				}
			}catch(Exception e){
//				DebugLog.e(TAG, "GetValueByName ("+ sName +") --> sValue = nodelist.item(0).getFirstChild().getNodeValue() error.");
			}
		}
		if(sValue==null)
		{
//			DebugLog.w(TAG, sName+"节点值sValue==null, Return");
		}
		return sValue;
	}
	
	public void AddChilds(String sExpress, String sName, String[] sChilds, String[] sValue, int iSize)
	{
		Node RootNode, MainNode, TempNode;

		RootNode = GetNode(sExpress);

		try
		{
			if(sName == "")
			{
				MainNode = RootNode;
			}
			else
			{
				MainNode = m_Document.createElement(sName);
			}
			for(int i = 0;i< iSize; i++)
			{
				TempNode = m_Document.createElement(sChilds[i]);

				TempNode.setNodeValue(sValue[i]);
				MainNode.appendChild(TempNode);
			}
			if(sName !="")
			{
				RootNode.appendChild(MainNode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String GetXMLToString()
	{
	   
		return m_DocRoot.toString();

	}

	static public String GetSendTextXML(String[] sChilds, String[] sValue, int iSize)
	{
		String sXml = null;
		sXml = "<Log>";
		for(int i=0;i<iSize;i++)
		{
			sXml = sXml+"<"+sChilds[i]+">"+sValue[i]+"</"+sChilds[i]+">";
		}
		sXml = sXml+"</Log>";
		return sXml;
	}
}
