package com.im.web.dao;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

import com.im.web.base.BaseDao;
import com.im.web.base.Page;
import com.im.web.entity.Tmpl;

@Repository
public class DowloadDao extends BaseDao<Tmpl, String> {

	protected final String GET_ALL_TMPL = " from Tmpl ";
	protected final String FIND_TMPL_BY_NAME = " select tmpl_id"
	+"   from (select tmpl_id from pmo_tmpl where real_name = ?  order by create_time desc )"
	+"  where rownum=1";

	protected final String GET_TMPL_BY_ID = " from Tmpl t where t.tmplId = ? ";

	public void saveOrUpdateTmpl(Tmpl tmpl) {
		this.save(tmpl);
	}

	public Page<Tmpl> getAllTmpl(Page<Tmpl> page, Object... values) {
		return findPage(page, GET_ALL_TMPL, values);
	}
		
	public Tmpl getTmplById(String tmpl){
		return findUnique(GET_TMPL_BY_ID, tmpl);
	}
	
	public void deleteTmplById(String tmpl){
		delete(tmpl);
	}
	
	public void deleteTmplByName(String tmpl){
		List<Map<String, Object>> list = getJdbcTemplate().queryForList(FIND_TMPL_BY_NAME, new Object[]{tmpl});
		if(list.size()>0){
			Map<String ,Object> map = list.get(0);
			String tid = map.get("TMPL_ID").toString();
			Tmpl t = getTmplById(tid);
			delete(t);
			destroyTmpl(t);
		}
	}

	private void destroyTmpl(Tmpl t) {
		File file = new File(t.getPath());
		if(file.exists()){
			file.delete();
		}
	}
}

