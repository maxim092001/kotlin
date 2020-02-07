// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.find.findUsages;

import com.intellij.psi.PsiElement;
import com.intellij.usages.UsageInfoToUsageConverter;
import org.jetbrains.annotations.NotNull;

final class LastSearchedData {

  private final UsageInfoToUsageConverter.TargetElementsDescriptor myDescriptor;
  private final FindUsagesOptions myOptions;

  LastSearchedData(@NotNull PsiElement @NotNull [] primaryElements,
                   @NotNull PsiElement @NotNull [] secondaryElements,
                   @NotNull FindUsagesOptions options) {
    myDescriptor = new UsageInfoToUsageConverter.TargetElementsDescriptor(primaryElements, secondaryElements);
    myOptions = options;
  }

  @NotNull PsiElement @NotNull [] getPrimaryElements() {
    return myDescriptor.getPrimaryElements();
  }

  @NotNull PsiElement @NotNull [] getSecondaryElements() {
    return myDescriptor.getAdditionalElements();
  }

  @NotNull FindUsagesOptions getOptions() {
    return myOptions;
  }
}
