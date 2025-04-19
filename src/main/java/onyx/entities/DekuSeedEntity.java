package onyx.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import onyx.items.ZeldaItems;

public class DekuSeedEntity extends PersistentProjectileEntity implements FlyingItemEntity {
    private static double damage = 1;

    public DekuSeedEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public DekuSeedEntity(World world, LivingEntity owner) {
        super(ZeldaEntities.DEKU_SEED_ENTITY, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
    }

    @Override
    public ItemStack getStack() {
        return new ItemStack(ZeldaItems.DEKU_SEED);
    }

    @Override
    public ItemStack getDefaultItemStack() {
        return new ItemStack(ZeldaItems.DEKU_SEED);
    }

    @Override
    public ItemStack asItemStack() {
        return getDefaultItemStack();
    }

    @SuppressWarnings("deprecation") // NOTE: sidedDamage is deprecated
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        double damageAmount = DekuSeedEntity.damage;

        // Create the damage source
        Entity owner = this.getOwner();
        DamageSource damageSource = this.getDamageSources().arrow(this, (Entity)(owner != null ? owner : this));
    
        // Apply damage to the entity
        boolean damageDealt = entity.sidedDamage(damageSource, (float) damageAmount);

        // If the entity was damaged
        if (damageDealt) {
            // Handle the effects after damage
            if (entity instanceof LivingEntity livingEntity) {
                // Apply knockback
                this.knockback(livingEntity, damageSource);
                
                // Call any additional on-hit behavior
                this.onHit(livingEntity);
            }
            
            // Play custom sound (Deku seed hit sound)
            this.playSound(SoundEvents.BLOCK_BAMBOO_HIT, 1.0F, 1.0F);
        }

        // Discard the seed entity after it hits something
        this.discard();
}

    @Override
    protected void onCollision(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
        this.playSound(SoundEvents.BLOCK_BAMBOO_HIT, 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
		if (type == HitResult.Type.ENTITY) {
			EntityHitResult entityHitResult = (EntityHitResult)hitResult;
			Entity entity = entityHitResult.getEntity();
			if (entity.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof ProjectileEntity projectileEntity) {
				projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this.getOwner(), this.getOwner(), true);
			}
			this.onEntityHit(entityHitResult);
		} else if (type == HitResult.Type.BLOCK) {
            this.discard();
		}
    }
}
