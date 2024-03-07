package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {


    @Autowired
    private SpecGroupMapper specGroupMapper; //商品规格组

    @Autowired
    private SpecParamMapper specParamMapper; //商品规格参数


    /**
     * 根据分类的id 查询对应的规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {

        //设置查询条件
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        //根据非空字段进行查询
        List<SpecGroup> list = specGroupMapper.select(specGroup);

        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOND);
        }

        return list;
    }

    /**
     * 根据规格组的id 查询对应的参数
     * @param gid
     * @return
     */
    public List<SpecParam> queryParamsList(Long gid, Long cid, Boolean searching) {

        //无需判断，如果没有值就是null，根据非空字段查询

        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);

        //根据非空字段进行查询
        List<SpecParam> list = specParamMapper.select(specParam);

        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.SPEC_PARAMS_NOT_FOND);
        }


        return list;
    }

    /**
     * 查询分类下的SpecGroup和对应的param
     * @param cid 分类id
     * @return List<SpecGroup>
     */
    public List<SpecGroup> querySpecGroupAndParamByCid(Long cid) {

        //1.根据分类id 查询group
        List<SpecGroup> groups = this.queryGroupsByCid(cid);

        if (CollectionUtils.isEmpty(groups)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOND);
        }
        //2.遍历group为其赋值 param参数
        //查询分类下的所有规格参数
        List<SpecParam> specParams = this.queryParamsList(null, cid, null);


        /*for (SpecGroup group : groups) {
            for (SpecParam specParam : specParams) {
                if (group.getId() == specParam.getGroupId()){
                    //是同一个，进行填充
                }
            }
        }*/

        //1.先把规格参数变为map，map的key为group的id，value为group下的所有规格参数
        Map<Long, List<SpecParam>> map = new HashMap<>();
        for (SpecParam specParam : specParams) {
            //1.判断map中是否存在gid
            if (!map.containsKey(specParam.getGroupId())){
                //这个组id，在map中不存在，新增一个list
                map.put(specParam.getGroupId(),new ArrayList<SpecParam>());
            }
            //去到list，如果存在新增，如果没存在，添加后新增
            map.get(specParam.getGroupId()).add(specParam);
        }

        //2.填充param到group
        for (SpecGroup specGroup : groups) {
            //从map中拿到List，进行数据的填充
            specGroup.setParams(map.get(specGroup.getId()));
        }

        return groups;
    }
}
