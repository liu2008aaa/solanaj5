/* This is free and unencumbered software released into the public domain. */

package org.near.borshj;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

public interface Borsh {
  public static @NonNull byte[] serialize(final @NonNull Object object) {
    return BorshBuffer.allocate(4096).write(requireNonNull(object)).toByteArray();
  }

  public static @NonNull <T> T deserialize(final @NonNull byte[] bytes, final @NonNull Class klass) {
    return deserialize(BorshBuffer.wrap(requireNonNull(bytes)), klass);
  }

  public static @NonNull <T> T deserialize(final @NonNull BorshBuffer buffer, final @NonNull Class klass) {
    return buffer.read(requireNonNull(klass));
  }

  public static boolean isSerializable(final @Nullable Class klass) {
    if (klass == null) return false;
    return Arrays.stream(klass.getInterfaces()).anyMatch(iface -> iface == Borsh.class) ||
      isSerializable(klass.getSuperclass());
  }
}
