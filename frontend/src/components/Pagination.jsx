import React from "react";
import { Pagination } from "react-bootstrap";

const PaginationControls = ({ page, setPage, hasNextPage }) => {
    return (
        <Pagination className="justify-content-center">
            <Pagination.Prev
                onClick={() => setPage((prev) => Math.max(prev - 1, 1))}
                disabled={page === 1}
            />
            <Pagination.Item active>{page}</Pagination.Item>
            <Pagination.Next
                onClick={() => setPage((prev) => prev + 1)}
                disabled={!hasNextPage}
            />
        </Pagination>
    );
};

export default PaginationControls;
