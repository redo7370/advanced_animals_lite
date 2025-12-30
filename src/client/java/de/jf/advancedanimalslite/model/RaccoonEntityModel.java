package de.jf.advancedanimalslite.model;

import de.jf.advancedanimalslite.render.RaccoonEntityRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * Entity Model for the Raccoon.
 * UV mapping based on Fox texture (48x32).
 */
public class RaccoonEntityModel extends EntityModel<RaccoonEntityRenderState> {
    
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    
    public RaccoonEntityModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }
    
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        
        // Kopf - UV(1, 5), Box 8x6x6, origin(0, 14.5, -3) - 2px höher
        ModelPartData headData = modelPartData.addChild("head",
            ModelPartBuilder.create()
                .uv(1, 5)
                .cuboid(-4.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F),
            ModelTransform.origin(0.0F, 14.5F, -3.0F)
        );
        
        // Rechtes Ohr - UV(8, 1), Box 2x2x1
        headData.addChild("right_ear",
            ModelPartBuilder.create()
                .uv(8, 1)
                .cuboid(-4.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F),
            ModelTransform.NONE
        );
        
        // Linkes Ohr - UV(15, 1), Box 2x2x1
        headData.addChild("left_ear",
            ModelPartBuilder.create()
                .uv(15, 1)
                .cuboid(2.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F),
            ModelTransform.NONE
        );
        
        // Nase/Schnauze - UV(6, 18), Box 4x2x3
        headData.addChild("nose",
            ModelPartBuilder.create()
                .uv(6, 18)
                .cuboid(-2.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F),
            ModelTransform.NONE
        );
        
        // Körper - UV(24, 15), Box 6x11x6, rotiert um 90° (PI/2)
        // Origin bei (0, 16, 0), rotiert, so dass Y-Achse zur Z-Achse wird
        ModelPartData bodyData = modelPartData.addChild("body",
            ModelPartBuilder.create()
                .uv(24, 15)
                .cuboid(-3.0F, -3.5F, -3.0F, 6.0F, 11.0F, 6.0F),
            ModelTransform.of(0.0F, 16.0F, 0.0F, 1.5707964F, 0.0F, 0.0F)
        );
        
        // Schwanz - UV(30, 0), Box 4x9x5, horizontal nach hinten zeigend
        bodyData.addChild("tail",
            ModelPartBuilder.create()
                .uv(30, 0)
                .cuboid(-2.0F, 0.0F, -2.5F, 4.0F, 9.0F, 5.0F),
            ModelTransform.of(0.0F, 7.5F, 0.0F, 0.0F, 0.0F, 0.0F)
        );
        
        // Beine mit leichter Dilation für Z-Fighting
        Dilation legDilation = new Dilation(0.001F);
        
        // Rechtes Hinterbein - UV(13, 24), origin(-2, 18, 5)
        modelPartData.addChild("right_hind_leg",
            ModelPartBuilder.create()
                .uv(13, 24)
                .cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, legDilation),
            ModelTransform.origin(-2.0F, 18.0F, 5.0F)
        );
        
        // Linkes Hinterbein - UV(4, 24), origin(2, 18, 5)
        modelPartData.addChild("left_hind_leg",
            ModelPartBuilder.create()
                .uv(4, 24)
                .cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, legDilation),
            ModelTransform.origin(2.0F, 18.0F, 5.0F)
        );
        
        // Rechtes Vorderbein - UV(13, 24), origin(-2, 18, -2)
        modelPartData.addChild("right_front_leg",
            ModelPartBuilder.create()
                .uv(13, 24)
                .cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, legDilation),
            ModelTransform.origin(-2.0F, 18.0F, -2.0F)
        );
        
        // Linkes Vorderbein - UV(4, 24), origin(2, 18, -2)
        modelPartData.addChild("left_front_leg",
            ModelPartBuilder.create()
                .uv(4, 24)
                .cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, legDilation),
            ModelTransform.origin(2.0F, 18.0F, -2.0F)
        );
        
        return TexturedModelData.of(modelData, 48, 32);
    }
    
    @Override
    public void setAngles(RaccoonEntityRenderState state) {
        super.setAngles(state);
        
        // Reset all rotations
        this.head.pitch = 0.0F;
        this.head.yaw = 0.0F;
        this.body.pitch = 1.5707964F; // Standard horizontal body position (90°)
        this.rightHindLeg.pitch = 0.0F;
        this.leftHindLeg.pitch = 0.0F;
        this.rightFrontLeg.pitch = 0.0F;
        this.leftFrontLeg.pitch = 0.0F;
        this.tail.pitch = 0.0F;
        
        // Sitz-Pose anwenden
        if (state.isSitting) {
            // Körper nach vorne neigen (aufrechter) - ca. 50° statt 90°
            this.body.pitch = 0.8726646F;
            // Körper 1 Einheit tiefer beim Sitzen
            this.body.setOrigin(0.0F, 17.0F, 0.0F);
            
            // Kopf tiefer positionieren beim Sitzen (1 Einheit tiefer: 12 -> 13)
            this.head.setOrigin(0.0F, 13.0F, -3.0F);
            
            // Kopf nach oben schauen (kompensiert die Körperneigung)
            this.head.pitch = -0.3490659F; // -20°
            
            // Hinterbeine: setOrigin() für tiefere Position und horizontal nach vorne
            // X-Positionen symmetrisch: -2.0 und 2.0 (wie im Model definiert)
            this.rightHindLeg.setOrigin(-2.0F, 23.0F, 5.0F); // Y tiefer (23 statt 18), Z=5
            this.leftHindLeg.setOrigin(2.0F, 23.0F, 5.0F);
            this.rightHindLeg.pitch = -1.5707964F; // -90° (horizontal nach VORNE zeigend)
            this.leftHindLeg.pitch = -1.5707964F;
            
            // Vorderbeine leicht nach vorne (aufgestützt)
            this.rightFrontLeg.pitch = -0.4363323F; // -25°
            this.leftFrontLeg.pitch = -0.4363323F;
            
            // Schwanz horizontal nach hinten (kompensiert Körperneigung, ~90° - 50° = 40°)
            this.tail.pitch = 0.6981317F; // +40° relativ zum Körper = horizontal
        } else {
            // Hinterbeine zurück zur normalen Position (wie im Model definiert)
            this.rightHindLeg.setOrigin(-2.0F, 18.0F, 5.0F);
            this.leftHindLeg.setOrigin(2.0F, 18.0F, 5.0F);
            
            // Körper zurück zur normalen Position
            this.body.setOrigin(0.0F, 16.0F, 0.0F);
            
            // Kopf zurück zur normalen Position
            this.head.setOrigin(0.0F, 14.5F, -3.0F);
            
            // Kopf-Rotation basierend auf Blickrichtung
            this.head.pitch = state.pitch * 0.017453292F;
            this.head.yaw = state.relativeHeadYaw * 0.017453292F;
            
            // Lauf-Animation basierend auf limbSwing
            float limbSwing = state.limbSwingAnimationProgress;
            float limbSwingAmount = state.limbSwingAmplitude;
            
            this.rightHindLeg.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.leftHindLeg.pitch = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
            this.rightFrontLeg.pitch = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount;
            this.leftFrontLeg.pitch = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        }
    }
}
