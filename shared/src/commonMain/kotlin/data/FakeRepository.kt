package data

class FakeRepository {

    suspend fun getData(): String {
        return "Hello Vince!"
    }

}