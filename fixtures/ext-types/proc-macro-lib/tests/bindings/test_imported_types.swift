// /* This Source Code Form is subject to the terms of the Mozilla Public
//  * License, v. 2.0. If a copy of the MPL was not distributed with this
//  * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import Foundation
import imported_types_lib

let semaphore = DispatchSemaphore(value: 0)
print("AAAAAAAAAAAAAAAAAAAAAAA 6.1")
print("AAAAAAAAAAAAAAAAAAAAAAA 6.2")
Task {
    print("BBBBBBBBBBBBBBBBBBBB 1")
    // This async function comes from the `uniffi-one` crate
    let uniffiOneEnum = await getUniffiOneAsync()
    print("BBBBBBBBBBBBBBBBBBBB 2")
    assert(uniffiOneEnum == UniffiOneEnum.one)
    print("BBBBBBBBBBBBBBBBBBBB 3")

    // This async function comes from the `proc-macro-lib` crate
    let uniffiOneType = await getUniffiOneTypeAsync(t: UniffiOneType(sval: "hello"))
    print("BBBBBBBBBBBBBBBBBBBB 4")
    assert(uniffiOneType.sval == "hello")
    print("BBBBBBBBBBBBBBBBBBBB 5")

    semaphore.signal()

    print("BBBBBBBBBBBBBBBBBBBB 6")
}
print("AAAAAAAAAAAAAAAAAAAAAAA 6.3")
semaphore.wait()
print("AAAAAAAAAAAAAAAAAAAAAAA 6.4")

// let semaphore = DispatchSemaphore(value: 0)
// print("test 1")
// Task {
//     print("test 1.1")
//     semaphore.signal()
//     print("test 1.2")
// }
// print("test 2")
// semaphore.wait()
// print("test 3")

// print("AAAAAAAAAAAAAAAAAAAAAAA 1")
// fflush(stdout)

// import imported_types_lib
// import Foundation

// print("AAAAAAAAAAAAAAAAAAAAAAA 2")
// fflush(stdout)

// let ct = getCombinedType(value: nil)
// assert(ct.uot.sval == "hello")
// assert(ct.guid ==  "a-guid")
// assert(ct.url ==  URL(string: "http://example.com/"))

// print("AAAAAAAAAAAAAAAAAAAAAAA 3")
// fflush(stdout)

// let ct2 = getCombinedType(value: ct)
// assert(ct == ct2)

// assert(getObjectsType(value: nil).maybeInterface == nil)
// assert(getObjectsType(value: nil).maybeTrait == nil)
// assert(getUniffiOneTrait(t: nil) == nil)

// print("AAAAAAAAAAAAAAAAAAAAAAA 4")
// fflush(stdout)

// let url = URL(string: "http://example.com/")!;
// assert(getUrl(url: url) ==  url)
// assert(getMaybeUrl(url: url)! == url)
// assert(getMaybeUrl(url: nil) == nil)
// assert(getUrls(urls: [url]) == [url])
// assert(getMaybeUrls(urls: [url, nil]) == [url, nil])

// print("AAAAAAAAAAAAAAAAAAAAAAA 5")
// fflush(stdout)

// assert(getUniffiOneType(t: UniffiOneType(sval: "hello")).sval == "hello")
// assert(getMaybeUniffiOneType(t: UniffiOneType(sval: "hello"))!.sval == "hello")
// assert(getMaybeUniffiOneType(t: nil) == nil)
// assert(getUniffiOneTypes(ts: [UniffiOneType(sval: "hello")]) == [UniffiOneType(sval: "hello")])
// assert(getMaybeUniffiOneTypes(ts: [UniffiOneType(sval: "hello"), nil]) == [UniffiOneType(sval: "hello"), nil])

// print("AAAAAAAAAAAAAAAAAAAAAAA 6")
// fflush(stdout)

// let semaphore = DispatchSemaphore(value: 0)
// print("AAAAAAAAAAAAAAAAAAAAAAA 6.1")
// print("AAAAAAAAAAAAAAAAAAAAAAA 6.2")
// Task {
//     print("BBBBBBBBBBBBBBBBBBBB 1")
//     // This async function comes from the `uniffi-one` crate
//     let uniffiOneEnum = await getUniffiOneAsync()
//     print("BBBBBBBBBBBBBBBBBBBB 2")
//     assert(uniffiOneEnum == UniffiOneEnum.one)
//     print("BBBBBBBBBBBBBBBBBBBB 3")

//     // This async function comes from the `proc-macro-lib` crate
//     let uniffiOneType = await getUniffiOneTypeAsync(t: UniffiOneType(sval: "hello"))
//     print("BBBBBBBBBBBBBBBBBBBB 4")
//     assert(uniffiOneType.sval == "hello")
//     print("BBBBBBBBBBBBBBBBBBBB 5")

//     semaphore.signal()

//     print("BBBBBBBBBBBBBBBBBBBB 6")
// }
// print("AAAAAAAAAAAAAAAAAAAAAAA 6.3")
// semaphore.wait()
// print("AAAAAAAAAAAAAAAAAAAAAAA 6.4")

// print("AAAAAAAAAAAAAAAAAAAAAAA 7")
// fflush(stdout)

// assert(getUniffiOneProcMacroType(t: UniffiOneProcMacroType(sval: "hello from proc-macro world")).sval == "hello from proc-macro world")
// assert(getMyProcMacroType(t: UniffiOneProcMacroType(sval: "proc-macros all the way down")).sval == "proc-macros all the way down")

// print("AAAAAAAAAAAAAAAAAAAAAAA 8")
// fflush(stdout)

// assert(getUniffiOneEnum(e: UniffiOneEnum.one) == UniffiOneEnum.one)
// assert(getMaybeUniffiOneEnum(e: UniffiOneEnum.one)! == UniffiOneEnum.one)
// assert(getMaybeUniffiOneEnum(e: nil) == nil)
// assert(getUniffiOneEnums(es: [UniffiOneEnum.one]) == [UniffiOneEnum.one])
// assert(getMaybeUniffiOneEnums(es: [UniffiOneEnum.one, nil]) == [UniffiOneEnum.one, nil])

// print("AAAAAAAAAAAAAAAAAAAAAAA 9")
// fflush(stdout)

// let g = getGuidProcmacro(g: nil)
// assert(g == getGuidProcmacro(g: g))

// print("AAAAAAAAAAAAAAAAAAAAAAA 10")
// fflush(stdout)

// // Thread.sleep(forTimeInterval: 1)

// // print("AAAAAAAAAAAAAAAAAAAAAAA 11")
// // fflush(stdout)
