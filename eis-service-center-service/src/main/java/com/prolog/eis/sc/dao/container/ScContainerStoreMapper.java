package com.prolog.eis.sc.dao.container;

import com.prolog.eis.model.route.stock.ContainerStore;
import com.prolog.eis.sc.dto.supply.ContainerStoreDto;
import com.prolog.framework.dao.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: wuxl
 * @create: 2021-08-19 15:45
 * @Version: V1.0
 */
@Repository
public interface ScContainerStoreMapper extends BaseMapper<ContainerStore> {
    /**
     * 找所有托盘位置
     *
     * @return
     */
    @Select({"select c.id, \r\n" +
            "c.container_no containerNo, \r\n" +
            "c.container_type containerType, \r\n" +
            "c.task_type taskType, \r\n" +
            "t.target_area areaNo \r\n" +
            "from container_store c, container_location t \r\n" +
            "where c.container_no = t.container_no"})
    List<ContainerStoreDto> findListDto();
}
