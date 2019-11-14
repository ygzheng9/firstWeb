package com.demo.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseProjectMat<M extends BaseProjectMat<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setProject(java.lang.String project) {
		set("project", project);
	}
	
	public java.lang.String getProject() {
		return getStr("project");
	}

	public void setPartNum(java.lang.String partNum) {
		set("partNum", partNum);
	}
	
	public java.lang.String getPartNum() {
		return getStr("partNum");
	}

	public void setReused(java.lang.Integer reused) {
		set("reused", reused);
	}
	
	public java.lang.Integer getReused() {
		return getInt("reused");
	}

}
