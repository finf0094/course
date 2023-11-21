package kz.course.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


public class Course {
    private Long id;
    private String title;
    private String description;

    private List<Topic> videos;
}
