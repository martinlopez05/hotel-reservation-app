import CustomDescription from "@/components/custom/CustomDescription"
import HotelGrid from "../components/HotelGrid"
import Searches from "../components/Searches"
import useHotels from "../Hooks/useHotels"

export default function HomePage() {

    const { data: hotels } = useHotels();

    return (
        <div>
            <CustomDescription></CustomDescription>
            <Searches hotels={hotels ? hotels : []}></Searches>
            <HotelGrid hotels={hotels ? hotels : []}></HotelGrid>
        </div>
    )
}
