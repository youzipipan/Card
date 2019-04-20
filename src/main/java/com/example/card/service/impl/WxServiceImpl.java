package com.example.card.service.impl;

import com.example.card.entities.Student;
import com.example.card.model.SaveModel;
import com.example.card.repository.StudentRepository;
import com.example.card.service.WxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

@Service("wxService")
public class WxServiceImpl implements WxService {

    @Resource
    private StudentRepository studentRepository;

    @Transactional
    @Override
    public Student findByOpenId(String openId) {

        Student student = studentRepository.findByOpenId(openId);

        return student;
    }

    @Transactional
    @Override
    public Student findByCardNumberAndPassWord(String cardNumber, String password) {

        Student student = studentRepository.findByCardNumberAndPassWord(cardNumber,password);
        return student;
    }

    @Transactional
    @Override
    public int updateByOpenId(String cardNumber, String passWord, String openId) {

        int i = studentRepository.updateByCardNumberAndPassWord(cardNumber,passWord,openId);
        if(i!=0){
            return i;
        }else {
            return 0;
        }
    }

    @Transactional
    @Override
    public Student findByStudentId(String studentId) {

        Student student = studentRepository.findByStudentId(studentId);
        return student;
    }

    @Override
    public String save(SaveModel saveModel) {

        Student student = new Student();
        student.setStudentId(UUID.randomUUID().toString().replace("-",""));
        student.setName(saveModel.getStudentName());
        student.setFlag("0");
        student.setDepartment(saveModel.getDepartment());
        student.setSpecialized(saveModel.getSpecialized());
        student.setGrade(saveModel.getGrade());
        student.setStudentClass(saveModel.getStudentClass());
        student.setStudentNumber(saveModel.getStudentNumber());
        student.setPhone(saveModel.getPhone());
        student.setIdnumber(saveModel.getIDNumber());
        student.setPassWord(saveModel.getPassWord());
        student.setSex(saveModel.getSex());
        String cardNumber = getTel();
        student.setCardNumber(cardNumber);
        studentRepository.save(student);
        return cardNumber;
    }

    private static String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
    private static String getTel() {
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String third=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+third;
    }
    public static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }

}
