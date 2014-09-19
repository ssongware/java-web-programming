package next.dao;

import javax.annotation.Resource;

import next.model.LogInfo;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

@Repository("logInfoDao")
public class LogInfoDao {
	@Resource(name="sqlSession")
	private SqlSession sqlSession;
	
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	public void create(LogInfo logInfo) {
		sqlSession.insert("LogInfo.create", logInfo);
	}
}