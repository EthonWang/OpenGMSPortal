package njgis.opengms.portal.entity.doo.modelItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njgis.opengms.portal.enums.RelationTypeEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelRelation {

    String oid;
    RelationTypeEnum relation;
}