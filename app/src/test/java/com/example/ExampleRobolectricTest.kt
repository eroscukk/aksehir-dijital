package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.NasreddinStoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Akşehir Rehberi", appName)
  }

  @Test
  fun `test nasreddin stories room crud operations`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    
    // Create an in-memory database configuration for isolated testing
    val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    val dao = db.travelDao()

    val testStory = NasreddinStoryEntity(
      storyId = "test_story_1",
      titleTr = "Test Başlık",
      titleEn = "Test Title",
      storyTr = "Türkçe hikaye metni",
      storyEn = "English story body",
      wisdomTr = "Türkçe çıkarım",
      wisdomEn = "English insight",
      audioDuration = "1:30",
      isLiked = false,
      isRead = false,
      personalNotes = ""
    )

    // Insert
    dao.insertStories(listOf(testStory))

    // Retrieve via Flow
    val stories = dao.getAllStoriesFlow().first()
    assertEquals(1, stories.size)
    assertEquals("test_story_1", stories[0].storyId)
    assertEquals("Test Title", stories[0].titleEn)

    // Update isLiked
    dao.toggleStoryLike("test_story_1", true)
    var updatedStories = dao.getAllStoriesFlow().first()
    assertTrue(updatedStories[0].isLiked)

    // Update isRead
    dao.markStoryRead("test_story_1", true)
    updatedStories = dao.getAllStoriesFlow().first()
    assertTrue(updatedStories[0].isRead)

    // Update personal notes
    dao.updateStoryNotes("test_story_1", "Gerçekten çok komik ve bilgece.")
    updatedStories = dao.getAllStoriesFlow().first()
    assertEquals("Gerçekten çok komik ve bilgece.", updatedStories[0].personalNotes)

    db.close()
  }
}

