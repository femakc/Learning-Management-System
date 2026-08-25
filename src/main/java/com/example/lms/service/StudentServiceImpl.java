package com.example.lms.service;

import com.example.lms.dao.GroupRepository;
import com.example.lms.dao.StudentRepository;
import com.example.lms.dto.StudentRequestDto;
import com.example.lms.dto.StudentResponseDto;
import com.example.lms.exceptions.ResourceNotFoundException;
import com.example.lms.mappers.StudentMapper;
import com.example.lms.models.Group;
import com.example.lms.models.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public StudentResponseDto saveStudent(StudentRequestDto studentRequestDto) {
        Student student = studentMapper.toEntity(studentRequestDto);
        Set<Group> findGroups = groupRepository.findAllByExternalIdIn(studentRequestDto.groupIds());

        if (findGroups.size() != studentRequestDto.groupIds().size()) {
            throw new ResourceNotFoundException("Одна или несколько указанных групп не найдены");
        }
        student.setGroups(findGroups);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponseDto(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto findStudentByExternalId(UUID externalId) {
        Student student = studentRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Студент с ID " + externalId + " не найден"
                        ))
                ;
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public StudentResponseDto updateStudentByExternalId(
            UUID externalId,
            StudentRequestDto studentRequestDto) {

        Student student = studentRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студент с ID " + externalId + " не найден"
                ))
                ;
        studentMapper.updateEntityFromDto(studentRequestDto, student);
        if (studentRequestDto.groupIds() != null) {
            Set<Group> groups = groupRepository.findAllByExternalIdIn(studentRequestDto.groupIds());
            if (groups.size() != studentRequestDto.groupIds().size()) {
                throw new ResourceNotFoundException(
                        "Одна или несколько групп не найдены"
                );
            }
            student.setGroups(groups);
        }

        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public void deleteStudentByExternalId(UUID externalId) {
        Student student = studentRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студент с ID " + externalId + " не найден"
                ))
                ;
        student.setDeleted(true);
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public StudentResponseDto restoreStudent(UUID externalId) {
        Student student = studentRepository.findByExternalIdAny(externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студента с ID " + externalId + " не найдено!"
                ));
        student.setDeleted(false);

        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public Page<StudentResponseDto> findAllStudentWithPagination(
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage.map(studentMapper::toResponseDto);
    }

    @Override
    @Transactional
    public StudentResponseDto unitedStudentOfGroup(UUID externalIdStudent, UUID externalIdGroup) {
        Student student = studentRepository.findByExternalId(externalIdStudent)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студента с ID " + externalIdStudent + " не существует"
                ));
        Group group = groupRepository.findAnyByExternalId(externalIdGroup)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группы с ID " + externalIdGroup + " не существует"
                ));
        student.getGroups().add(group);
        studentRepository.save(student); //TODO возможно save излишний
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public StudentResponseDto removeStudentOfGroup(UUID externalIdStudent, UUID externalIdGroup) {
        Student student = studentRepository.findByExternalId(externalIdStudent)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Студента с ID " + externalIdStudent + " не существует"
                ));
        Group group = groupRepository.findAnyByExternalId(externalIdGroup)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Группы с ID " + externalIdGroup + " не существует"
                ));

        if (!student.getGroups().contains(group)) {
            throw new ResourceNotFoundException("Студент не состоит в указанной группе");
        }

        student.getGroups().remove(group);
        studentRepository.save(student);
        return studentMapper.toResponseDto(student);
    }
}
