
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { HotelsTable } from "./components/Hotel-table"
import { RoomsTable } from "./components/Rooms-table"

export default function AdminPage() {
    return (
        <div className="container mx-auto py-8 px-4">
            <div className="mb-8">
                <h1 className="text-3xl font-bold mb-2">Panel de Administración</h1>
                <p className="text-muted-foreground">Gestiona hoteles y habitaciones</p>
            </div>

            <Tabs defaultValue="hotels" className="w-full">
                <TabsList className="grid w-full max-w-md grid-cols-2">
                    <TabsTrigger value="hotels">Hoteles</TabsTrigger>
                    <TabsTrigger value="rooms">Habitaciones</TabsTrigger>
                </TabsList>

                <TabsContent value="hotels" className="mt-6">
                    <HotelsTable />
                </TabsContent>

                <TabsContent value="rooms" className="mt-6">
                    <RoomsTable />
                </TabsContent>
            </Tabs>
        </div>
    )
}
