-- 业主表
DROP TABLE IF EXISTS `owner_info`;
CREATE TABLE `owner_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `owner_no` VARCHAR(255) COMMENT '业主编码',
  `owner_name` VARCHAR(255) COMMENT '业主名称',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '业主表' ROW_FORMAT = Dynamic;

-- 商品资料表
DROP TABLE IF EXISTS `goods_info`;
CREATE TABLE `goods_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `owner_id` int(11) COMMENT '业主ID',
  `goods_code` varchar(225) COMMENT '商品编码',
  `goods_name` varchar(225) COMMENT '商品名称',
  `big_package_barcode1` varchar(225) COMMENT '大包装条码1',
  `big_package_barcode2` varchar(225) COMMENT '大包装条码2',
  `mid_package_barcode1` varchar(225) COMMENT '中包装条码1',
  `mid_package_barcode2` varchar(225) COMMENT '中包装条码2',
  `small_package_barcode1` varchar(225) COMMENT '小包装条码1',
  `small_package_barcode2` varchar(225) COMMENT '小包装条码2',
  `big_package_quantity` double(11,2) COMMENT '大包装数量',
  `mid_package_quantity` double(11,2) COMMENT '中包装数量',
  `small_package_quantity` double(11,2) COMMENT '小包装数量',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品资料表' ROW_FORMAT = Dynamic;


-- 商品批次表
DROP TABLE IF EXISTS `goods_lot_info`;
CREATE TABLE `goods_lot_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `goods_id` int(2) COMMENT '商品ID',
  `lot_no` int(11) COMMENT '批次号',
  `expire_time` date COMMENT '有效期',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '商品批次表' ROW_FORMAT = Dynamic;

--  容器任务单策略配置
DROP TABLE IF EXISTS `container_task_strategy`;
CREATE TABLE `container_task_strategy`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_task_type_no` varchar(64) COMMENT '容器任务单类型编号',
  `type_name` varchar(64) COMMENT '容器任务单类型名称',
  `priority` int(3) COMMENT '优先级',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单策略配置' ROW_FORMAT = Dynamic;

--  容器任务单策略源区域表
DROP TABLE IF EXISTS `container_task_strategy_source_area`;
CREATE TABLE `container_task_strategy_source_area`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_task_strategy_id` varchar(64) COMMENT '容器任务单策略ID',
  `area_no` varchar(64) COMMENT '区域编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单策略源区域表' ROW_FORMAT = Dynamic;


--  容器任务单策略目标区域表
DROP TABLE IF EXISTS `container_task_strategy_target_area`;
CREATE TABLE `container_task_strategy_target_area`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_task_strategy_id` varchar(64) COMMENT '容器任务单策略ID',
  `area_no` varchar(64) COMMENT '区域编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单策略目标区域表' ROW_FORMAT = Dynamic;


--  容器任务单
DROP TABLE IF EXISTS `container_task`;
CREATE TABLE `container_task`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `type_no` varchar(64) COMMENT '容器任务单类型编号',
  `status` int(2) COMMENT '任务单状态(0未开始、1进行中、2已完成、3已暂停、4已取消)',
  `create_time` datetime COMMENT '创建时间',
  `priority` int(11) COMMENT '优先级',
  `task_start_time` datetime COMMENT '任务开始时间',
  `task_finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单' ROW_FORMAT = Dynamic;

--  容器任务单明细表
DROP TABLE IF EXISTS `container_task_detail`;
CREATE TABLE `container_task_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_task_id` int(11) COMMENT '容器任务单ID',
  `upper_system_task_deail_id` varchar(64) COMMENT '上游系统任务单明细ID',
  `container_no` varchar(64) COMMENT '容器号',
  `status` int(2) COMMENT '任务单状态(0未开始、1进行中、2已完成、3已暂停、4已取消)',
  `source_area` varchar(64) COMMENT '起始区域',
  `source_location` varchar(64) COMMENT '起始坐标',
  `target_area` varchar(64) COMMENT '目标区域',
  `target_location` varchar(64) COMMENT '目标坐标',
  `create_time` datetime COMMENT '创建时间',
  `detail_start_time` datetime COMMENT '任务明细开始时间',
  `detail_finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单明细表' ROW_FORMAT = Dynamic;

--  容器任务单回告表
DROP TABLE IF EXISTS `container_task_report`;
CREATE TABLE `container_task_report`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_task_id` int(11) COMMENT '容器任务单ID',
  `type_no` varchar(64) COMMENT '容器任务单类型编号',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `create_time` datetime COMMENT '创建时间',
  `report_time` datetime COMMENT '回告时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单回告表' ROW_FORMAT = Dynamic;

--  容器任务历史单
DROP TABLE IF EXISTS `container_task_his`;
CREATE TABLE `container_task_his`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `type_no` varchar(64) COMMENT '容器任务单类型编号',
  `status` int(2) COMMENT '任务单状态(0未开始、1进行中、2已完成、3已暂停、4已取消)',
  `create_time` datetime COMMENT '创建时间',
  `priority` int(11) COMMENT '优先级',
  `task_start_time` datetime COMMENT '任务开始时间',
  `task_finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务历史单' ROW_FORMAT = Dynamic;

--  容器任务单明细历史表
DROP TABLE IF EXISTS `container_task_detail_his`;
CREATE TABLE `container_task_detail_his`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `container_task_id` int(11) COMMENT '容器任务单ID',
  `upper_system_task_deail_id` varchar(64) COMMENT '上游系统任务单明细ID',
  `container_no` varchar(64) COMMENT '容器号',
  `status` int(2) COMMENT '任务单状态(0未开始、1进行中、2已完成、3已暂停、4已取消)',
  `source_area` varchar(64) COMMENT '起始区域',
  `source_location` varchar(64) COMMENT '起始坐标',
  `target_area` varchar(64) COMMENT '目标区域',
  `target_location` varchar(64) COMMENT '目标坐标',
  `create_time` datetime COMMENT '创建时间',
  `detail_start_time` datetime COMMENT '任务明细开始时间',
  `detail_finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单明细历史表' ROW_FORMAT = Dynamic;

--  容器任务单回告历史表
DROP TABLE IF EXISTS `container_task_report_his`;
CREATE TABLE `container_task_report_his`  (
  `id` int(11) NOT NULL COMMENT 'id',
  `container_task_id` int(11) COMMENT '容器任务单ID',
  `type` int(2) COMMENT '业务类型',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单明细ID',
  `create_time` datetime COMMENT '创建时间',
  `report_time` datetime COMMENT '回告时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '容器任务单回告历史表' ROW_FORMAT = Dynamic;



-- 出库任务单表
DROP TABLE IF EXISTS `outbound_task_order`;
CREATE TABLE `outbound_task_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_task_summary_id` int(11) COMMENT '库存任务汇总单ID',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `outbound_task_type_no` varchar(64) COMMENT '出库任务单类型编号',
  `picking_order_id` int(11) COMMENT '批拣单ID',
  `order_pool_id` int(11) COMMENT '订单池ID',
  `state` int(2) COMMENT '任务单状态（未开始、进行中、已暂停、已完成）',
  `is_short_picking` int(2) COMMENT '是否短拣',
  `priority` int(11) COMMENT '优先级',
  `create_time` datetime COMMENT '创建时间',
  `start_time` datetime COMMENT '任务开始时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
)ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单表' ROW_FORMAT = Dynamic;



-- 出库任务单明细表
DROP TABLE IF EXISTS `outbound_task_detail`;
CREATE TABLE `outbound_task_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_task_order_id` int(11) COMMENT '库存任务汇总ID',
  `lot_id` int(11) COMMENT '批次',
  `goods_id` int(11) COMMENT '商品ID',
  `plan_num` double(11,2) COMMENT '计划数量',
  `actual_num` double(11,2) COMMENT '实际数量',
  `is_finish` int(11) COMMENT '是否完成',
  `is_short_picking` int(2) COMMENT '是否短拣',
  `create_time` datetime COMMENT '创建时间',
  `start_time` datetime COMMENT '任务开始时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单明细表' ROW_FORMAT = Dynamic;

-- 出库任务单容器绑定表
DROP TABLE IF EXISTS `outbound_binding_task`;
CREATE TABLE `outbound_binding_task`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_no` varchar(64) COMMENT '容器号',
  `picking_order_id` int(11) COMMENT '批拣单ID',
  `station_id` int(11) COMMENT '站台ID',
  `order_pool_id` int(11) COMMENT '订单池ID',
  `create_time` datetime COMMENT '创建时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单容器绑定表' ROW_FORMAT = Dynamic;

-- 出库任务单容器绑定明细表
DROP TABLE IF EXISTS `outbound_binding_task_detail`;
CREATE TABLE `outbound_binding_task_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_binding_task_id` int(11) COMMENT '容器绑定表ID',
  `container_no` varchar(64) COMMENT '容器号',
  `container_no_sub` varchar(64) COMMENT '子容器号',
  `outbound_task_order_id` int(11) COMMENT '出库任务单明细ID',
  `goods_id` int(11) COMMENT '商品ID',
  `lot_id` int(11) COMMENT '批次ID',
  `binding_num` double(11,2) COMMENT '绑定数量',
  `create_time` datetime COMMENT '创建时间',
  `finish_time` datetime COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单容器绑定明细表' ROW_FORMAT = Dynamic;


-- 出库任务单回告表
DROP TABLE IF EXISTS `outbound_task_report`;
CREATE TABLE `outbound_task_report`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_task_id` int(11) COMMENT '容器绑定表ID',
  `outbound_task_type_no` varchar(64) COMMENT '出库任务单类型编号',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `create_time` datetime COMMENT '创建时间',
  `report_time` datetime COMMENT '回告时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单回告表' ROW_FORMAT = Dynamic;

-- 出库任务单历史表
DROP TABLE IF EXISTS `outbound_task_order_his`;
CREATE TABLE `outbound_task_order_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_task_summary_id` int(11) COMMENT '库存任务汇总单ID',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `outbound_task_type_no` varchar(64) COMMENT '出库任务单类型编号',
  `picking_order_id` int(11) COMMENT '批拣单ID',
  `order_pool_id` int(11) COMMENT '订单池ID',
  `state` int(2) COMMENT '任务单状态（未开始、进行中、已暂停、已完成）',
  `is_short_picking` int(2) COMMENT '是否短拣',
  `priority` int(11) COMMENT '优先级',
  `create_time` datetime COMMENT '创建时间',
  `start_time` datetime COMMENT '任务开始时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
)ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单历史表' ROW_FORMAT = Dynamic;


-- 出库任务单明细历史表
DROP TABLE IF EXISTS `outbound_task_detail_his`;
CREATE TABLE `outbound_task_detail_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_task_order_id` int(11) COMMENT '库存任务汇总ID',
  `lot_id` int(11) COMMENT '批次',
  `goods_id` int(11) COMMENT '商品ID',
  `plan_num` double(11,2) COMMENT '计划数量',
  `actual_num` double(11,2) COMMENT '实际数量',
  `is_finish` int(11) COMMENT '是否完成',
  `is_short_picking` int(2) COMMENT '是否短拣',
  `create_time` datetime COMMENT '创建时间',
  `start_time` datetime COMMENT '任务开始时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单明细历史表' ROW_FORMAT = Dynamic;

-- 出库任务单容器绑定历史表
DROP TABLE IF EXISTS `outbound_binding_task_his`;
CREATE TABLE `outbound_binding_task_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_no` varchar(64) COMMENT '容器号',
  `picking_order_id` int(11) COMMENT '批拣单ID',
  `station_id` int(11) COMMENT '站台ID',
  `order_pool_id` int(11) COMMENT '订单池ID',
  `create_time` datetime COMMENT '创建时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单容器绑定历史表' ROW_FORMAT = Dynamic;

-- 出库任务单容器绑定明细历史表
DROP TABLE IF EXISTS `outbound_binding_task_detail_his`;
CREATE TABLE `outbound_binding_task_detail_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_binding_task_id` int(11) COMMENT '容器绑定表ID',
  `container_no` varchar(64) COMMENT '容器号',
  `container_no_sub` varchar(64) COMMENT '子容器号',
  `outbound_task_order_id` int(11) COMMENT '出库任务单明细ID',
  `goods_id` int(11) COMMENT '商品ID',
  `lot_id` int(11) COMMENT '批次ID',
  `binding_num` double(11,2) COMMENT '绑定数量',
  `create_time` datetime COMMENT '创建时间',
  `finish_time` datetime COMMENT '完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单容器绑定明细历史表' ROW_FORMAT = Dynamic;


-- 出库任务单回告历史表
DROP TABLE IF EXISTS `outbound_task_report_his`;
CREATE TABLE `outbound_task_report_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_task_id` int(11) COMMENT '容器绑定表ID',
  `outbound_task_type_no` varchar(64) COMMENT '出库任务单类型编号',
  `upper_system_task_id` varchar(255) COMMENT '上游系统任务单ID',
  `create_time` datetime COMMENT '创建时间',
  `report_time` datetime COMMENT '回告时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单回告历史表' ROW_FORMAT = Dynamic;



-- 出库任务单策略配置
DROP TABLE IF EXISTS `outbound_strategy_config`;
CREATE TABLE `outbound_strategy_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_no` varchar(225) COMMENT '出库任务单类型编号',
  `type_name` varchar(225) COMMENT '出库任务单类型名称',
  `out_model` varchar(225) COMMENT '出库模式(批拣单出库、订单池出库)',
  `dispatch_priority` int(11) COMMENT '调度优先级(调度执行顺序)',
  `compose_order_config` varchar(225) COMMENT '组单策略配置',
  `max_order_num` int(11) COMMENT '最大组单数量',
  `max_order_volume` int(11) COMMENT '最大组单体积',
  `store_matching_strategy` int(2) COMMENT '库存匹配策略(1.按品种、2.按品批)',
  `outbound_expiry_date_rate` int(3) COMMENT '优先出库最高时效百分比',
  `prohibit_expiry_date_rate` int(3) COMMENT '禁止出库最高时效百分比',
  `clear_store_strategy` int(2) COMMENT '清库存策略(0.否 1.是)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单策略配置' ROW_FORMAT = Dynamic;


-- 出库任务单策略源区域表
DROP TABLE IF EXISTS `outbound_task_strategy_source_area`;
CREATE TABLE `outbound_task_strategy_source_area`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_strategy_config_id` int(11) COMMENT '出库任务单策略ID',
  `area_no` varchar(225) COMMENT '区域编号',
  `priority` int(3) COMMENT '优先级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单策略源区域表' ROW_FORMAT = Dynamic;

-- 出库任务单策略目标站台表
DROP TABLE IF EXISTS `outbound_task_strategy_target_station`;
CREATE TABLE `outbound_task_strategy_target_station`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_strategy_config_id` int(11) COMMENT '出库任务单策略ID',
  `station_id` int(11) COMMENT '目标站台',
  `priority` int(3) COMMENT '优先级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务单策略目标站台表' ROW_FORMAT = Dynamic;




-- 出库任务汇总单拆单策略配置
DROP TABLE IF EXISTS `outbound_split_strategy_config`;
CREATE TABLE `outbound_split_strategy_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `strategy_name` varchar(225) COMMENT '策略名称',
  `strategy_type_no` varchar(225) COMMENT '策略类型编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库任务汇总单拆单策略配置' ROW_FORMAT = Dynamic;

-- 出库汇总任务单拆单策略明细表
DROP TABLE IF EXISTS `outbound_split_strategy_config_detail`;
CREATE TABLE `outbound_split_strategy_config_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `outbound_split_strategy_config_id` int(2) COMMENT '拆单策略配置Id',
  `area_no` varchar(64) COMMENT '区域',
  `split_strategy` varchar(225) COMMENT '拆单策略',
  `sort_index` int(2) COMMENT '排序',
  `avg_outbound_time` datetime COMMENT '平均出库时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存任务汇总单拆单策略明细配置' ROW_FORMAT = Dynamic;


-- 出库汇总任务单
DROP TABLE IF EXISTS `out_summary_order`;
CREATE TABLE `out_summary_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_no` varchar(64) COMMENT '出库汇总任务单类型编号',
  `state` int(2) COMMENT '任务单状态(未开始、进行中、已暂停、已完成)',
  `is_short_picking` int(2) COMMENT '是否短拣',
  `create_time` datetime COMMENT '创建时间',
  `start_time` datetime COMMENT '任务开始时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库汇总任务单' ROW_FORMAT = Dynamic;

-- 出库汇总任务单历史表
DROP TABLE IF EXISTS `out_summary_order_his`;
CREATE TABLE `out_summary_order_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_no` varchar(64) COMMENT '出库汇总任务单类型编号',
  `state` int(2) COMMENT '任务单状态(未开始、进行中、已暂停、已完成)',
  `is_short_picking` int(2) COMMENT '是否短拣',
  `create_time` datetime COMMENT '创建时间',
  `start_time` datetime COMMENT '任务开始时间',
  `finish_time` datetime COMMENT '任务完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '出库汇总任务单历史表' ROW_FORMAT = Dynamic;

-- 批拣单表
DROP TABLE IF EXISTS `picking_order`;
CREATE TABLE `picking_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `is_all_arrive` int(2) COMMENT '是否容器全部到达（0.否 1.是）',
  `start_time` datetime COMMENT '开始时间',
  `finish_time` datetime COMMENT '完成时间',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '批拣单表' ROW_FORMAT = Dynamic;

-- 批拣单历史表
DROP TABLE IF EXISTS `picking_order_his`;
CREATE TABLE `picking_order_his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `is_all_arrive` int(2) COMMENT '是否容器全部到达（0.否 1.是）',
  `start_time` datetime COMMENT '开始时间',
  `finish_time` datetime COMMENT '完成时间',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '批拣单历史表' ROW_FORMAT = Dynamic;


-- 实时汇总单表
DROP TABLE IF EXISTS `order_pool_config`;
CREATE TABLE `order_pool_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) COMMENT '实时汇总单表名称',
  `type_no` varchar(64) COMMENT '出库任务单类型编号',
  `max_order_num` int(11) COMMENT '最大汇单数量',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '实时汇总单表' ROW_FORMAT = Dynamic;



-- 库存 -----
-- 容器库存表
DROP TABLE IF EXISTS `container_store`;
CREATE TABLE `container_store` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `container_no` varchar(225) NOT NULL COMMENT '容器号',
  `container_type` int(11) DEFAULT NULL COMMENT '容器类型 -1空托剁 0非整托 1整托',
  `task_type` int(11) DEFAULT NULL COMMENT '任务类型 0无业务任务 10非整托入库 11整托入库 12移库入库 20非整托出库 21整托出库 22演示出库 25空托出库',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `containerNo` (`container_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='容器库存表';

-- 子容器库存表
DROP TABLE IF EXISTS `container_store_sub`;
CREATE TABLE `container_store_sub` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `container_store_id` int(11) COMMENT '母容器库存ID',
  `container_store_sub_no` varchar(255) DEFAULT NULL COMMENT '子容器号',
  `owner_id` int(11)  COMMENT '业主ID',
  `goods_id` int(11)   COMMENT '商品ID',
  `lot_id` int(11)  COMMENT '批次ID',
  `qty` double(11,2)  COMMENT '数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '子容器库存表' ROW_FORMAT = Dynamic;

-- 库存变更记录账单表
DROP TABLE IF EXISTS `store_accounting`;
CREATE TABLE `store_accounting` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `container_store_no` varchar(255) DEFAULT NULL COMMENT '容器号',
  `container_store_sub_no` varchar(255) DEFAULT NULL COMMENT '子容器编号',
  `change_qty` double(11,2)  COMMENT '变更数量',
  `qty` double(11,2)  COMMENT '现有库存数量',
  `case_specs` int(11)  COMMENT '箱规格',
  `box_specs` int(11)  COMMENT '包装规格',
  `goods_id` int(11) DEFAULT NULL  COMMENT '商品ID',
  `lot_id` int(11) DEFAULT NULL COMMENT '批次ID', 
  `task_type` int(11) DEFAULT NULL COMMENT '任务类型', 
  `task_no` varchar(255) DEFAULT NULL COMMENT '任务号', 
  `outbound_task_detail_id` int(11) DEFAULT 0 COMMENT '出库任务单明细ID', 
  `remark` varchar(255) DEFAULT NULL COMMENT '备注', 
  `create_time` datetime  COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存变更记录账单表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `store_area_location`;
CREATE TABLE `store_area_location` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `store_area` varchar(20) DEFAULT NULL COMMENT '库存区域',
  `is_available` int(11) DEFAULT NULL COMMENT '是否可用（0否 1是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存区域货位关系汇总配置表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `store_area_location_detail`;
CREATE TABLE `store_area_location_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `store_area_location_id` int(11) DEFAULT NULL COMMENT '汇总id',
  `associate_area` varchar(20) DEFAULT NULL COMMENT '关联区域',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '库存区域货位关系明细配置表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `store_supply_config`;
CREATE TABLE `store_supply_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `source_area` varchar(20) DEFAULT NULL COMMENT '起始区域',
  `target_area` varchar(20) DEFAULT NULL COMMENT '目标区域',
  `is_available` int(11) COMMENT '是否开启（0否 1是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '安全库存补货策略配置' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `store_supply_goods_config`;
CREATE TABLE `store_supply_goods_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `store_supply_config_id` int(11) COMMENT '汇总id',
  `goods_id` int(11) COMMENT '商品id',
  `qty` int(11) COMMENT '数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '安全库存补货商品策略配置' ROW_FORMAT = Dynamic;