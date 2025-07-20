package mtmt.MTMT_BE.domain.auth.application.dto.signup;

import mtmt.MTMT_BE.domain.user.domain.entity.User;

public record SignUpResponse(
        String email,
        String name,
        String role,
        String thumbnail,
        String location,
        String birthDate,
        String gender,
        int age
) {
    public SignUpResponse(User user) {
        this(
                user.getEmail(),
                user.getName(),
                user.getRole().toString(),
                user.getThumbnail(),
                user.getLocation() != null ? user.getLocation().toString() : null,
                user.getBirthDate().toString(),
                user.getGender().toString(),
                user.getAge()
        );
    }
}
