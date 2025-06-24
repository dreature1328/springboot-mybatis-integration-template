package dreature.smit.service.impl;

import dreature.smit.mapper.BaseMapper;
import dreature.smit.service.BaseService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    @Autowired
    protected SqlSession sqlSession;
    @Autowired
    protected BaseMapper<T> baseMapper;
}
