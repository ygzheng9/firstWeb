package com.demo.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {
	
	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("blog", "id", Blog.class);
		arp.addMapping("bom_item", "id", BomItem.class);
		arp.addMapping("bom_project_mapping", "id", BomProjectMapping.class);
		arp.addMapping("mat_info", "id", MatInfo.class);
		arp.addMapping("po_head", "id", PoHead.class);
		arp.addMapping("po_item", "id", PoItem.class);
		arp.addMapping("project_info", "id", ProjectInfo.class);
		arp.addMapping("project_mat", "id", ProjectMat.class);
		arp.addMapping("region_sales", "id", RegionSales.class);
		arp.addMapping("region_sales_stats", "id", RegionSalesStats.class);
		arp.addMapping("session", "id", Session.class);
		arp.addMapping("user", "id", User.class);
		arp.addMapping("wk_form_demo", "id", WkFormDemo.class);
		arp.addMapping("wk_instance", "id", WkInstance.class);
		arp.addMapping("wk_instance_step", "id", WkInstanceStep.class);
		arp.addMapping("wk_type", "id", WkType.class);
	}
}

