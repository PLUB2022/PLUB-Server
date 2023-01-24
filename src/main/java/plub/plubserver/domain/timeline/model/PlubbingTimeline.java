//package plub.plubserver.domain.timeline.model;
//
//import plub.plubserver.common.model.BaseTimeEntity;
//import plub.plubserver.domain.plubbing.model.Plubbing;
//import plub.plubserver.domain.todo.model.PlubbingTodo;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//public class PlubbingTimeline extends BaseTimeEntity {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "timeline_id")
//    private Long id;
//
//    private int totalLikes;
//    private int totalComments;
//
//    // 타임라인(다) - 모임(1)
//    @ManyToOne
//    @JoinColumn(name = "plubbing_id")
//    private Plubbing plubbing;
//
//    // 타임라인(1) - 투두(다)
//    @OneToMany(mappedBy = "timeLine", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PlubbingTodo> todoList = new ArrayList<>();
//
//}
