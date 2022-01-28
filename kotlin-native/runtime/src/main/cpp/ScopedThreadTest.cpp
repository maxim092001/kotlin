/*
 * Copyright 2010-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "ScopedThread.hpp"

#include <array>
#include <pthread.h>
#include <type_traits>

#include "gmock/gmock.h"
#include "gtest/gtest.h"

#include "KAssert.h"

using namespace kotlin;

namespace {

template <size_t NAME_SIZE = 100>
std::string threadName(pthread_t thread) {
    static_assert(
            std::is_invocable_r_v<int, decltype(pthread_getname_np), pthread_t, char*, size_t>, "Invalid pthread_getname_np signature");
    std::array<char, NAME_SIZE> name;
    int result = pthread_getname_np(thread, name.data(), name.size());
    RuntimeAssert(result == 0, "Failed to get thread name error: %d\n", result);
    // Make sure name is null-terminated.
    name[name.size() - 1] = '\0';
    return std::string(name.data());
}

} // namespace

TEST(ScopedThreadTest, ThreadName) {
    ScopedThread thread(ScopedThread::attributes().name("thread name for test"), [] {
        EXPECT_THAT(threadName(pthread_self()), "thread name for test");
    });
    EXPECT_THAT(threadName(thread.native_handle()), "thread name for test");
}
