package draylar.gofish.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import draylar.gofish.registry.GoFishLoot;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record MatchFishingRodCondition(List<Identifier> rods) implements LootCondition {

    public static final MapCodec<MatchFishingRodCondition> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Identifier.CODEC.listOf().fieldOf("rods").forGetter(MatchFishingRodCondition::rods)
                    )
                    .apply(instance, MatchFishingRodCondition::new)
    );

    @Override
    public LootConditionType getType() {
        return GoFishLoot.MATCH_FISHING_ROD;
    }

    @Override
    public Set<ContextParameter<?>> getAllowedParameters() {
        return ImmutableSet.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        return Optional.ofNullable(lootContext.get(LootContextParameters.TOOL))
                .map(ItemStack::getItem)
                .flatMap(item -> Registries.ITEM.getEntry(item).getKey())
                .map(RegistryKey::getValue)
                .map(rods::contains)
                .orElse(false);
    }

    public static LootCondition.Builder builder(Identifier... rods) {
        return () -> new MatchFishingRodCondition(List.of(rods));
    }
}
