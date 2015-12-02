package zx.soft.sina.IO.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import zx.soft.impala.adt.core.ConstADT;
import zx.soft.impala.adt.core.DataTrans;
import zx.soft.impala.adt.core.Tools;
import zx.soft.sina.IO.domain.AccessList;
import zx.soft.sina.IO.domain.AlertList;
import zx.soft.sina.IO.domain.PlcClient;
import zx.soft.sina.IO.domain.QueryParameters;
import zx.soft.sina.IO.util.ImpalaConnection;
import zx.soft.sina.IO.util.JsonUtils;

@Service
public class ImpalaService {

	public static Logger logger = LoggerFactory.getLogger(ImpalaService.class);

	public AccessList getSpecAccess(String tableName, String rowkey) {
		String sqlStatement = "SELECT * FROM " + tableName + " WHERE rowkey =\'" + rowkey + "\'";
		AccessList accessList = null;
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				try {
					while (resultSet.next()) {
						accessList = DataTrans.resultSet2Access(resultSet);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		} catch (Exception e1) {
			throw new RuntimeException();
		}

		return accessList;
	}

	public AlertList getSpecAlert(String tableName, String rowkey) {
		String sqlStatement = "SELECT * FROM " + tableName + " WHERE rowkey =\'" + rowkey + "\'";
		AlertList alertList = null;
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				try {
					while (resultSet.next()) {
						alertList = DataTrans.resultSet2AlertList(resultSet);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		} catch (Exception e1) {
			throw new RuntimeException();
		}

		return alertList;
	}

	public PlcClient getSpecPlcClient(String tableName, String rowkey) {
		String sqlStatement = "SELECT * FROM " + tableName + " WHERE rowkey =\'" + rowkey + "\'";
		PlcClient plcClient = null;
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				try {
					while (resultSet.next()) {
						plcClient = DataTrans.resultSet2PlcClient(resultSet);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException();
		} catch (Exception e1) {
			throw new RuntimeException();
		}

		return plcClient;
	}

	public List<AccessList> getAccessListQueryResult(String tableName, List<QueryParameters> queryParams,
			String orderBy, String order, int pageSize, int page) {
		String sqlStatement = Tools.getBasicSqlStatement(tableName, queryParams, orderBy, order, pageSize, page);
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			List<AccessList> temp = new ArrayList<>();
			if (resultSet != null) {
				try {
					while (resultSet.next()) {
						temp.add(DataTrans.resultSet2Access(resultSet));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return temp;
		} catch (SQLException e) {
			throw new RuntimeException();
		} catch (Exception e1) {
			throw new RuntimeException();
		}
	}

	public List<AlertList> getAlertListQueryResult(String tableName, List<QueryParameters> queryParams, String orderBy,
			String order, int pageSize, int page) {
		String sqlStatement = Tools.getBasicSqlStatement(tableName, queryParams, orderBy, order, pageSize, page);
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			List<AlertList> temp = new ArrayList<>();
			if (resultSet != null) {
				try {
					while (resultSet.next()) {
						temp.add(DataTrans.resultSet2AlertList(resultSet));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return temp;
		} catch (SQLException e) {
			throw new RuntimeException();
		} catch (Exception e1) {
			throw new RuntimeException();
		}
	}

	public List<PlcClient> getPlcClientQueryResult(String tableName, List<QueryParameters> queryParams, String orderBy,
			String order, int pageSize, int page) {
		String sqlStatement = Tools.getBasicSqlStatement(tableName, queryParams, orderBy, order, pageSize, page);
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			List<PlcClient> temp = new ArrayList<>();
			if (resultSet != null) {
				try {
					while (resultSet.next()) {
						temp.add(DataTrans.resultSet2PlcClient(resultSet));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return temp;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		} catch (Exception e1) {
			throw new RuntimeException();
		}
	}

	public int getSum(String tableName, List<QueryParameters> queryParams) {
		String condition = Tools.getPartSqlStatement(queryParams);
		String sqlStatement = "SELECT COUNT(*) FROM " + tableName + " WHERE " + condition;
		int s = 0;
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				while (resultSet.next()) {
					s = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}

	//对查询的结果根据某字段类型进行分类统计
	public String getStat(String tableName, List<QueryParameters> queryParams, String groupBy, int limit) {
		String condition = Tools.getPartSqlStatement(queryParams);
		String sqlStatement = "SELECT " + groupBy + ",COUNT(*) AS number FROM " + tableName + " WHERE " + condition
				+ " GROUP BY " + groupBy + " ORDER BY number DESC LIMIT " + limit;
		Map<String, Integer> map = new HashMap<>();
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				while (resultSet.next()) {
					map.put(resultSet.getString(1), resultSet.getInt(2));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return JsonUtils.toJson(map);
	}

	//用户一段时间上网趋势统计分析
	public String getTrendency(String tableName, long start, long end) {
		String sqlStatement = "SELECT (time-time%86400)-28800 AS DAY , COUNT(*) AS NUM FROM " + tableName
				+ " WHERE time BETWEEN " + start + " AND " + end + " GROUP BY DAY ORDER BY NUM";
		Map<Long, Integer> map = new HashMap<>();
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				while (resultSet.next()) {
					map.put(resultSet.getLong(1), resultSet.getInt(2));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return JsonUtils.toJson(map);
	}

	public String getAlertStats(String tableName, List<QueryParameters> queryParams, String groupBy, int limit) {
		String condition = Tools.getPartSqlStatement(queryParams);
		String sqlStatement = "SELECT " + groupBy + " , COUNT(*) AS NUM FROM " + tableName + " WHERE " + condition
				+ " GROUP BY " + groupBy + " ORDER BY NUM DESC LIMIT " + limit;
		logger.info(sqlStatement);
		Map<String, Integer> map = new HashMap<>();
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				while (resultSet.next()) {
					if (!groupBy.equals("rule_id")) {
						map.put(resultSet.getString(1), resultSet.getInt(2));
					} else {
						if (resultSet.getString(1) == null) {
							map.put("ruleId_is_null", resultSet.getInt(2));
						} else {
							String rule_name = getRuleNameById("\\'" + resultSet.getString(1) + "\\'");
							if (rule_name == null) {
								map.put(resultSet.getString(1), resultSet.getInt(2));
							} else {
								map.put(rule_name.substring(1, rule_name.length() - 1), resultSet.getInt(2));
							}
						}
					}

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return JsonUtils.toJson(map);
	}

	private String getRuleNameById(String ruleId) {
		String sqlStatement = "SELECT rule_name FROM " + ConstADT.TABLE_PLCNETINFO + " WHERE rule_id = " + "\""
				+ ruleId + "\"";
		String ruleName = null;
		logger.info(sqlStatement);
		try (Connection conn = ImpalaConnection.getConnection();
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery(sqlStatement);) {
			if (resultSet != null) {
				while (resultSet.next()) {
					if (resultSet.getString(1) != null) {
						ruleName = resultSet.getString(1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ruleName;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		ImpalaService service = new ImpalaService();
		String id = "34010101201507220211";
		String rule_id = "\\'" + id + "\\'";
		System.out.println(rule_id);
		System.out.println(service.getRuleNameById(rule_id));
		//System.out.println(service.getAlertStats("parquet_compression.alertlist", queryParams, "rule_id", 10));
	}
}
