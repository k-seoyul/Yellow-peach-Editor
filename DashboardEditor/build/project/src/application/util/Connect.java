package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import application.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Connect {
	private PoolManager pool;

	public Connect() {
		pool = PoolManager.getInstance();
	}

	public PoolManager getPool() {
		return pool;
	}

	// insert json file
	public void insertDashboard(String version, String dashBoardName, JSONObject obj) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int result = 0;
		int num = 0;

		String versionTable;
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";

		try {
			conn = pool.getConnection();
			System.out.println("Database connection established");

			String sql = "insert into " + versionTable + " (name, content, available) values(?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dashBoardName);
			pstmt.setString(2, obj.toString());
			pstmt.setInt(3, 1);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
	}
	
	// insert topic
	public boolean insertTopic(String topic, String ctrTopic) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		int result = 0;
		int num = 0;

		String versionTable;
		
		try {
			conn = pool.getConnection();
			System.out.println("Database connection established");

			String sql = "insert into topicList (topic, ctrTopic) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, topic);
			pstmt.setString(2, ctrTopic);

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			return false;
			//e.printStackTrace();
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.out.println("pstmt not closed");
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					/* ignore close errors */ }
			}
		}
		return true;
	}

	public Vector<String> getTopicList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<String> data = new Vector<String>();
		try {
			conn = pool.getConnection();
			String sql = "select * from topicList";
			pstmt = conn.prepareStatement(sql, rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();

			rs.last(); // 커서를 제일 끝 레코드로 보냄
			int total = rs.getRow(); // 현재 커서가 위치한 레코드 번호
			rs.beforeFirst();

			while (rs.next()) {
				data.add(rs.getString("topic"));
			}

			int i = 0;
			while (i < data.size()) {
				// System.out.println(data.get(i));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return data;
	}

	// topic과 일대일 매칭시킨 control topic 반환
	public String getCtrTopic(String firstTopic) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String ctrTopic = null;
		try {
			conn = pool.getConnection();
			String sql = "select * from topicList where topic = '" + firstTopic + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				ctrTopic = rs.getString("ctrtopic");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return ctrTopic;
	}
	
	// URL List 불러오기
	public Vector<String> getUrlTopicList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector<String> data = new Vector<String>();
		try {
			conn = pool.getConnection();
			String sql = "select * from urlList";
			pstmt = conn.prepareStatement(sql, rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();

			rs.last(); // 커서를 제일 끝 레코드로 보냄
			int total = rs.getRow(); // 현재 커서가 위치한 레코드 번호
			rs.beforeFirst();

			while (rs.next()) {
				data.add(rs.getString("url"));
			}

			int i = 0;
			while (i < data.size()) {
				// System.out.println(data.get(i));
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return data;
	}
	
	// Urltopic과 일대일 매칭시킨 Url control topic 반환
	public String getUrlCtrTopic(String firstTopic) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String ctrTopic = null;
		try {
			conn = pool.getConnection();
			String sql = "select * from urlList where url = '" + firstTopic + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				ctrTopic = rs.getString("ctrtopic");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return ctrTopic;
	}

	// 데이터베이스에 있는 대시보드json가져오기
	public Vector<JSONObject> getDashboard(String version){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String versionTable = null;
		Vector<JSONObject> dashboardVector = new Vector<JSONObject>();
		
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";
		
		JSONParser parser = new JSONParser();
		try {
			conn = pool.getConnection();
			String sql = "select * from "+ versionTable;
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Object obj = parser.parse(rs.getString("content"));
    			JSONObject jsonObject = (JSONObject) obj;
				dashboardVector.add(jsonObject);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return dashboardVector;
	}
	
	public boolean isDashBoardExist(String version, String dashboardName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		String versionTable;
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";

		try {
			conn = pool.getConnection();
			String sql = "select * from " + versionTable + " where name = '" + dashboardName + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}
	
	public boolean isTopicExist(String topic) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			conn = pool.getConnection();
			String sql = "select * from topicList where topic = '" + topic + "'";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}

	public int updateDashboard(String version, String dashboardName, JSONObject obj) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		String versionTable;
		if (version.equals("PC"))
			versionTable = "Json_pc";
		else
			versionTable = "Json_mobile";

		try {
			conn = pool.getConnection();
			String sql = "update " + versionTable + " set content=? where name = '" + dashboardName + "'";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, obj.toString());

			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt);
		}
		return result;
	}
	public boolean isConnect() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;

		try {
			if(pool.getConnection() != null)
				conn = pool.getConnection();
			else
				result = false;
			String sql = "select * from topicList";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = true;
			else
				result = false;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(conn, pstmt, rs);
		}
		return result;
	}
}
