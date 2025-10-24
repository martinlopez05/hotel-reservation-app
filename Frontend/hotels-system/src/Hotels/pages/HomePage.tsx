import CustomDescription from "@/components/custom/CustomDescription"
import HotelGrid from "../components/HotelGrid"
import Searches from "../components/Searches"
import useHotels from "../Hooks/useHotels"
import { useMemo, useState } from "react"

export default function HomePage() {
    const { data: hotels = [] } = useHotels()

    const [searchTerm, setSearchTerm] = useState("")
    const [locationFilter, setLocationFilter] = useState("all")


    const filteredHotels = useMemo(() => {
        return hotels.filter((hotel) => {
            const matchesSearch = hotel.name
                .toLowerCase()
                .includes(searchTerm.toLowerCase())
            const matchesLocation =
                locationFilter === "all" || hotel.location === locationFilter
            return matchesSearch && matchesLocation
        })
    }, [hotels, searchTerm, locationFilter])

    return (
        <div className="container mx-auto px-4 py-8">
            <CustomDescription />

            <Searches
                hotels={hotels}
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                locationFilter={locationFilter}
                setLocationFilter={setLocationFilter}
            />

            <HotelGrid hotels={filteredHotels} />
        </div>
    )
}
