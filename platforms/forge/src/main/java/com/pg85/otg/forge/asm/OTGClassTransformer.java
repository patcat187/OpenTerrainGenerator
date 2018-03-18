package com.pg85.otg.forge.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import scala.tools.asm.Type;
import static org.objectweb.asm.Opcodes.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.math.BlockPos;

public class OTGClassTransformer implements IClassTransformer
{
	static String[] classesBeingTransformed =
	{
		"net.minecraft.entity.EntityLivingBase",
		"net.minecraft.entity.item.EntityMinecart",
		"net.minecraft.entity.projectile.EntityArrow",
		"net.minecraft.entity.item.EntityBoat",
		"net.minecraft.entity.item.EntityFallingBlock",
		"net.minecraft.entity.item.EntityItem",
		"net.minecraft.entity.projectile.EntityLlamaSpit",
		"net.minecraft.entity.projectile.EntityShulkerBullet",
		"net.minecraft.entity.projectile.EntityThrowable",
		"net.minecraft.entity.item.EntityTntPrimed",
		"net.minecraft.entity.item.EntityXPOrb",
		"net.minecraft.entity.Entity",
	};

	@Override
	public byte[] transform(String name, String transformedName, byte[] classBeingTransformed)
	{
		if(name != null && transformedName != null)
		{
			boolean isObfuscated = !name.equals(transformedName);
			int index = -1;
			for(int i = 0; i < classesBeingTransformed.length; i++)
			{
				if(classesBeingTransformed[i].equals(transformedName))
				{
					index = i;
					break;
				}
			}
			return index != -1 ? transform(index, classBeingTransformed, isObfuscated) : classBeingTransformed;
		}
		return classBeingTransformed;
	}

	public byte[] transform(int index, byte[] classBeingTransformed, boolean isObfuscated)
	{
		//System.out.println("Transforming: " + classesBeingTransformed[index]);
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(classBeingTransformed);
			classReader.accept(classNode, 0);

			// Do the transformation
			switch(index)
			{
				case 0: // net.minecraft.entity.EntityLivingBase.travel
					transformTravel(classNode, isObfuscated);
				break;
				case 1: // net.minecraft.entity.item.EntityMinecart.onUpdate
					transformOnUpdateMineCart(classNode, isObfuscated);
				break;
				case 2: // net.minecraft.entity.projectile.EntityArrow.onUpdate
					transformOnUpdateArrow(classNode, isObfuscated);
				break;
				case 3: // net.minecraft.entity.item.EntityBoat.onUpdate
					transformOnUpdateBoat(classNode, isObfuscated);
				break;
				case 4: // net.minecraft.entity.item.EntityFallingBlock.onUpdate
					transformOnUpdateFallingBlock(classNode, isObfuscated);
				break;
				case 5: // net.minecraft.entity.item.EntityItem.onUpdate
					transformOnUpdateItem(classNode, isObfuscated);
				break;
				case 6: // net.minecraft.entity.projectile.EntityLlamaSpit.onUpdate
					transformOnUpdateLlamaSpit(classNode, isObfuscated);
				break;
				case 7: // net.minecraft.entity.projectile.EntityShulkerBullet.onUpdate
					transformOnUpdateShulkerBullet(classNode, isObfuscated);
				break;
				case 8: // net.minecraft.entity.projectile.EntityThrowable.onUpdate
					transformOnUpdateThrowable(classNode, isObfuscated);
				break;
				case 9: // net.minecraft.entity.item.EntityTntPrimed.onUpdate
					transformOnUpdateTntPrimed(classNode, isObfuscated);
				break;
				case 10: // net.minecraft.entity.item.EntityXPOrb.onUpdate
					transformOnUpdateXPOrb(classNode, isObfuscated);
				break;
				case 11: // net.minecraft.entity.Entity.updateFallState
					transformUpdateFallState(classNode, isObfuscated);
				break;
			}

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return classBeingTransformed;
	}

	// Gravity settings for players
	// net.minecraft.entity.EntityLivingBase.moveEntityWithHeading(float strafe, float forward)
	private void transformTravel(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "g" : "moveEntityWithHeading";
		String injectSnapShotDescriptor = isObfuscated ? "(FF)V" : "(FF)V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.08D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.08D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactor", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// Gravity settings for minecarts
	// net.minecraft.entity.item.EntityMinecart.onUpdate()
	private void transformOnUpdateMineCart(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.03999999910593033D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.03999999910593033D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorMineCart", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// Gravity settings for arrows
	// net.minecraft.entity.projectile.EntityArrow.onUpdate()
	private void transformOnUpdateArrow(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.05000000074505806D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.05000000074505806D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorArrow", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.item.EntityBoat.updateMotion()
	private void transformOnUpdateBoat(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "w" : "updateMotion";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == -0.03999999910593033D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorBoat", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.item.EntityFallingBlock.onUpdate
	private void transformOnUpdateFallingBlock(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.03999999910593033D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.03999999910593033D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorFallingBlock", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}
		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.item.EntityItem.onUpdate
	private void transformOnUpdateItem(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.03999999910593033D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() ==  0.03999999910593033D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorItem", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.projectile.LlamaSpit.onUpdate
	private void transformOnUpdateLlamaSpit(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.05999999865889549D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.05999999865889549D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorLlamaSpit", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.projectile.EntityShulkerBullet.onUpdate
	private void transformOnUpdateShulkerBullet(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.05000000074505806D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.04D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorShulkerBullet", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.projectile.EntityThrowable.getGravityVelocity
	private void transformOnUpdateThrowable(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "j" : "getGravityVelocity";
		String injectSnapShotDescriptor = isObfuscated ? "()F" : "()F";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//return 0.03F;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Float && ((Float)((LdcInsnNode)instruction).cst).floatValue() == 0.03F)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorThrowable", "(Lnet/minecraft/entity/Entity;)F", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.item.EntityTntPrimed.onUpdate",
	private void transformOnUpdateTntPrimed(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.03999999910593033D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.03999999910593033D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorTNTPrimed", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// net.minecraft.entity.item.EntityXPOrb.onUpdate
	private void transformOnUpdateXPOrb(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "A_" : "onUpdate";
		String injectSnapShotDescriptor = isObfuscated ? "()V" : "()V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				AbstractInsnNode instructionToRemove = null;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					//this.motionY -= 0.029999999329447746D;

					if(instruction.getOpcode() == LDC && ((LdcInsnNode)instruction).cst instanceof Double && ((Double)((LdcInsnNode)instruction).cst).doubleValue() == 0.029999999329447746D)
					{
						instructionToRemove = instruction;
						InsnList toInsert = new InsnList();

						toInsert.add(new VarInsnNode(ALOAD, 0));
						toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getGravityFactorXPOrb", "(Lnet/minecraft/entity/Entity;)D", false));
						method.instructions.insertBefore(instructionToRemove, toInsert);
						break;
					}
				}
				if(instructionToRemove != null)
				{
					method.instructions.remove(instructionToRemove);
				} else {
					throw new RuntimeException("OTG is not compatible with this version of Forge.");
				}
				return;
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}

	// Gravity settings for falling damage
	// net.minecraft.entity.Entity.updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
	private void transformUpdateFallState(ClassNode gameDataNode, boolean isObfuscated)
	{
		String injectSnapShot = isObfuscated ? "a" : "updateFallState";
		String injectSnapShotDescriptor = isObfuscated ? "(DZLatj;Lco;)V" : "(DZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;)V";

		for(MethodNode method : gameDataNode.methods)
		{
			if(method.name.equals(injectSnapShot) && method.desc.equals(injectSnapShotDescriptor))
			{
				boolean bFound = false;
				for(AbstractInsnNode instruction : method.instructions.toArray())
				{
					// this.fallDistance = (float)((double)this.fallDistance - y);
					// should be
					// this.fallDistance = (float)((double)this.fallDistance - (y * gravityFactor));
					// Where gravityFactor is between 0 and 1 and determines how much falling damage should be applied based on the gravity of the world.

					//mv.visitVarInsn(DLOAD, 1);

					if(instruction.getOpcode() == DLOAD)
					{
						// Only apply to the second DLOAD
						if(bFound)
						{
							// Insert new instruction
							InsnList toInsert = new InsnList();
							toInsert.add(new VarInsnNode(ALOAD, 0));
							toInsert.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(OTGHooks.class), "getFallDamageFactor", "(DLnet/minecraft/entity/Entity;)D", false));
							method.instructions.insertBefore(instruction.getNext(), toInsert);
							return;
						}
						bFound = true;
					}
				}
			}
		}

		throw new RuntimeException("OTG is not compatible with this version of Forge.");
	}
}