package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户不存在、密码不对、账号被锁定）
        //判断该用户是否存在
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //判断密码是否正确
        //对明文密码进行MD5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //判断账户是否被锁定
        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();

        //对象属性拷贝
        //将(employeeDTO中的属性拷贝给employee。属性名必须一致
        BeanUtils.copyProperties(employeeDTO,employee);

        //设置账户状态，默认正常状态
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认为123456，需要进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

//        //设置当前记录的创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //设置当前记录的创建人id和修改人的id
//        //利用ThreadLocal将JWT令牌中的id存入
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }


    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //select * from employee limit 0,10
        //开始分页查询，使用PageHelper插件，第一个参数：当前的页码，每页展示多少条数据
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //使用mapper查询数据库
        Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);
        //封装结果并返回
        long total = page.getTotal();   //总记录数
        List<Employee> records = page.getResult();  //当前页面的数据集合
        PageResult pageResult=new PageResult(total, records);
        return pageResult;
    }


    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
//        Employee employee=new Employee();
//        employee.setStatus(status);
//        employee.setId(id);

        //在实体类上加上注解@Builder
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);

    }


    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee=employeeMapper.getById(id);
        employee.setPassword("******");
        return employee;
    }


    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        //对象的属性拷贝，将第一个参数的属性拷贝到第二个参数里面去
        BeanUtils.copyProperties(employeeDTO,employee);
//        //设置最后修改时间
//        employee.setUpdateTime(LocalDateTime.now());
//        //设置修改人
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

    @Override
    @Transactional
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        if(passwordEditDTO.getOldPassword().equals(passwordEditDTO.getNewPassword())){
            throw new PasswordEditFailedException("新密码不能和旧密码一样");
        }

        //设置当前登录的员工的id
        passwordEditDTO.setEmpId(BaseContext.getCurrentId());
        //根据id查询员工
        Employee employee = employeeMapper.getById(passwordEditDTO.getEmpId());
        //查询旧密码是否正确
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        if(!employee.getPassword().equals(oldPassword)){
            throw new PasswordEditFailedException("原始密码输入错误。");
        }

        //设置新密码
        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
        Employee employee1=new Employee();
        employee1.setId(passwordEditDTO.getEmpId());
        employee1.setPassword(newPassword);
        employeeMapper.update(employee1);
    }
}
