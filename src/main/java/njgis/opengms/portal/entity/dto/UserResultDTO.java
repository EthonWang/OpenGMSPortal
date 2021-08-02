package njgis.opengms.portal.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResultDTO {

    String oid;//
    String name;
    String image;
    String userId;

}
