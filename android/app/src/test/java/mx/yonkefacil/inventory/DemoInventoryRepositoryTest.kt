package mx.yonkefacil.inventory

import kotlinx.coroutines.test.runTest
import mx.yonkefacil.inventory.data.DemoInventoryRepository
import mx.yonkefacil.inventory.data.InventoryStatus
import mx.yonkefacil.inventory.data.NewPart
import mx.yonkefacil.inventory.data.NewVehicle
import mx.yonkefacil.inventory.data.PartCondition
import mx.yonkefacil.inventory.data.SaveResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoInventoryRepositoryTest {
    @Test
    fun `valid PIN signs in known staff`() = runTest {
        val repository = DemoInventoryRepository()
        assertEquals("Ana", repository.signIn("staff-ana", "1234")?.name)
        assertEquals(null, repository.signIn("staff-ana", "0000"))
    }

    @Test
    fun `vehicle can be received`() = runTest {
        val repository = DemoInventoryRepository()
        val result = repository.addVehicle(NewVehicle(2020, "Toyota", "Corolla", "Gris", "", "Junto al portón"))
        assertTrue(result is SaveResult.Saved)
        assertEquals(4, repository.vehicles.value.size)
    }

    @Test
    fun `published part requires photo`() = runTest {
        val repository = DemoInventoryRepository()
        val result = repository.addPart(
            NewPart("v-1001", "Marcha", "Starter", "Eléctrico", "Electrical", PartCondition.Tested, "Estante", 1000, true, false),
        )
        assertTrue(result is SaveResult.Failed)
    }

    @Test
    fun `sale is atomic and owner can restore it`() = runTest {
        val repository = DemoInventoryRepository()
        val first = repository.sellPart("p-1042", "staff-ana")
        val duplicate = repository.sellPart("p-1042", "staff-ana")
        assertTrue(first is SaveResult.Saved)
        assertTrue(duplicate is SaveResult.Failed)
        assertEquals(InventoryStatus.Sold, repository.parts.value.first { it.id == "p-1042" }.status)

        val restored = repository.restorePart("p-1042")
        assertNotNull((restored as? SaveResult.Saved)?.value)
        assertEquals(InventoryStatus.Available, repository.parts.value.first { it.id == "p-1042" }.status)
    }
}
