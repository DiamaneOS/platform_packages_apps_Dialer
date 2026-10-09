/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.dialer.precall;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import com.android.dialer.callintent.CallIntentBuilder;
import com.android.dialer.phonenumberutil.PhoneNumberHelper;
import com.android.dialer.telecom.TelecomUtil;
import com.android.dialer.util.DialerUtils;

/** Interface to prepare a {@link CallIntentBuilder} before placing the call with telecom. */
public interface PreCall {

  /**
   * @return a intent when started as activity, will perform the pre-call actions and then place a
   *     call. TODO(twyen): if all actions do not require an UI, return a intent that will place the
   *     call directly instead.
   */
  @NonNull
  @MainThread
  Intent buildIntent(Context context, CallIntentBuilder builder);

  static Intent getIntent(Context context, CallIntentBuilder builder) {
    return PreCallComponent.get(context).getPreCall().buildIntent(context, builder);
  }

  static void start(Context context, CallIntentBuilder builder) {
    DialerUtils.startActivityWithErrorToast(context, getIntent(context, builder));
  }

  /**
   * Whether a long press on a call action may offer the account chooser for {@code number}: there
   * are at least two call-capable accounts and the number is a phone number (not SIP) and not an
   * emergency number. Emergency numbers are left to the platform to route.
   */
  static boolean canChooseAccount(Context context, String number) {
    if (TextUtils.isEmpty(number)
        || PhoneNumberHelper.isUriNumber(number)
        || PhoneNumberUtils.isEmergencyNumber(number)) {
      return false;
    }
    return TelecomUtil.getCallCapablePhoneAccounts(context).size() > 1;
  }
}
