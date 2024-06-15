![Frame 12](https://github.com/w36495/senty/assets/52291662/cbe7cd8b-2f90-4fbc-bdc4-33b4c83aca91)

# 🎁 Senty
소중한 사람들과 정성스럽게 주고받은 선물을 기록할 수 있는 어플리케이션  

</br>

⏳ 개발 기간
---
2022.01. ~ 2022.03. (v1)  
2024.05. ~ 2024.06. (v2)  

</br>

🚀 PlayStore
---
[PlayStore 이동](https://play.google.com/store/apps/details?id=com.w36495.senty)

</br>

🛠 기술 스택
---
- Kotlin, Layered Architecture
- MVVM(Model-View-ViewModel) + Repository Pattern
- Firebase realtime database, storage, authentication
- AAC ViewModel
- Coroutine + Flow
- Glide
- Hilt
- View -> 100% Compose

**사용한 외부 라이브러리**
- [Calendar](https://github.com/vsnappy1/ComposeDatePicker)
- [Naver-map-compose](https://github.com/fornewid/naver-map-compose)

</br>  

🤖 기능  
---
### 1️⃣ 회원  
|이메일로 로그인|이메일로 회원가입|비밀번호 재설정|  
|--|--|--|
|![로그인](https://github.com/w36495/Senty/assets/52291662/abe361c2-4cac-4995-a0a7-76580076816b)|![회원가입](https://github.com/w36495/Senty/assets/52291662/e6a209ed-2e37-4e1d-99b3-8ecebd23e23c)|![비밀번호재설정](https://github.com/w36495/Senty/assets/52291662/a18337aa-6b61-451e-821e-29404a238af1)|  
   
### 2️⃣ 친구  
|친구 목록|친구 등록/수정|친구 조회|친구 삭제|
|-|-|-|-|
|![친구목록](https://github.com/w36495/senty/assets/52291662/e0864ce1-259e-413e-a1b7-7c2a2dd890b8)|![친구등록](https://github.com/w36495/senty/assets/52291662/ecfd04db-daa3-4248-a9f5-c15aa1e50056)|![친구조회](https://github.com/w36495/senty/assets/52291662/f0074f85-180d-4f18-85b6-4647f7585fa1)|![친구삭제](https://github.com/w36495/senty/assets/52291662/516a85f3-93ee-42d1-9cb8-e354821649fb)

### 3️⃣ 친구 그룹  
|그룹 목록|그룹 등록|그룹 수정|그룹 삭제|
|--|--|--|--|
|![친구그룹목록](https://github.com/w36495/senty/assets/52291662/b472b361-37ff-44ad-8dca-852adb7baf37)|![친구그룹등록](https://github.com/w36495/senty/assets/52291662/027eb1ac-4528-4c28-a317-a09606eae1f6)|![친구그룹수정](https://github.com/w36495/senty/assets/52291662/1e16c350-d002-452d-be8f-1eae2d4c68ae)|![친구그룹삭제](https://github.com/w36495/senty/assets/52291662/8ad7be18-26da-4b56-bdb0-327dd3970db1)|


### 4️⃣ 선물  
- **선물등록**

|카메라/앨범 선택|선물 카테고리 선택|친구 선택|날짜 선택|
|--|--|--|--|
|![사진선택](https://github.com/w36495/senty/assets/52291662/0f48c165-c7c8-4553-88a9-1bf74755bdba)|![선물카테고리](https://github.com/w36495/senty/assets/52291662/3838c7b5-2844-49c1-9acb-3f218bf2d579)|![친구선택](https://github.com/w36495/senty/assets/52291662/96e2b1f2-3cb2-4021-a9d1-19f6454a9278)|![날짜선택](https://github.com/w36495/senty/assets/52291662/ef3b2f1d-6609-423d-a157-b794279d17b0)|

- **선물목록**

|전체 선물 목록|받은 선물 목록|준 선물 목록|
|--|--|--|
|![선물목록(전체)](https://github.com/w36495/senty/assets/52291662/547b0057-0f80-4432-b9c1-258888fbe04e)|![선물목록(받은선물)](https://github.com/w36495/senty/assets/52291662/fabd8a37-06b3-4ba5-8869-07d21ff0c59b)|![선물목록(준선물)](https://github.com/w36495/senty/assets/52291662/53275d8b-ff0c-4669-b768-9f90cbe31d18)|

- **선물 조회/수정/삭제**

|선물 조회|선물 수정(수정중)|선물 삭제|  
|--|--|--|
|![선물조회](https://github.com/w36495/senty/assets/52291662/46dfb4d5-47f1-4ea0-9f38-0f7f6777c55b)|![선물조회](https://github.com/w36495/senty/assets/52291662/60b6ef5f-e993-4e51-81ae-78be6e0dcae1)|![선물삭제](https://github.com/w36495/senty/assets/52291662/c2b5b312-000d-4015-a58f-13273384b832)


### 5️⃣ 기념일  

|기념일목록|기념일등록/수정|기념일장소검색|기념일조회|기념일삭제|  
|--|--|--|--|--|
|![기념일](https://github.com/w36495/senty/assets/52291662/a05060ef-ad48-4d0a-ada5-d6465b6d1d3d)|![기념일등록](https://github.com/w36495/senty/assets/52291662/61db44dd-8a0e-4528-aef6-1189fc68a7d7)|![장소선택](https://github.com/w36495/senty/assets/52291662/36504ebf-0d02-464a-a204-982649844c6e)|![기념일조회](https://github.com/w36495/senty/assets/52291662/4dacffd4-aeeb-4ec8-8566-ba25b8e853ee)|![기념일삭제](https://github.com/w36495/senty/assets/52291662/f31d5c3b-cb72-4287-a293-a76dfed13307)|



