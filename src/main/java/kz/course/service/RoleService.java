package kz.course.service;


import kz.course.entity.Role;
import kz.course.exceptions.NotFoundException;
import kz.course.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("role not found")
        );
    }

}
