import * as React from "react";
import Box from "@mui/material/Box";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Divider from "@mui/material/Divider";
import styled from "styled-components";
import ArrowRightIcon from "@mui/icons-material/ArrowRight";
import ViewOpeion from "./ViewOption";

export default function SelectedListItem() {
  const category = [
    "전체",
    "스마트폰",
    "태블릿",
    "웨어러블기기",
    "BESPOKE",
    "주방가전",
    "생활가전",
    "TV&오디오",
    "노트북/PC",
  ];
  const [selectedIndex, setSelectedIndex] = React.useState(1);

  const handleListItemClick = (
    event: React.MouseEvent<HTMLDivElement, MouseEvent>,
    index: number
  ) => {
    setSelectedIndex(index);
  };

  const StyledH5 = styled.h3`
    text-align: center;
  `;
  return (
    <div style={{position:'fixed'}}>
    <Box
      sx={{
        width: "100%",
        maxWidth: 360,
        bgcolor: "background.paper",
        border: "1px solid #DDDDDD",
      }}
    >
      <List component="nav" aria-label="main mailbox folders">
        <StyledH5 style={{ textAlign: "center" }}>category</StyledH5>
        <Divider />

        {category.map((category, i) => (
          <ListItemButton
            selected={selectedIndex === i}
            onClick={(event) => handleListItemClick(event, i)}
          >
            {selectedIndex === i && (
              <ListItemIcon>
                <ArrowRightIcon />
              </ListItemIcon>
            )}
            <ListItemText primary={category} />
          </ListItemButton>
        ))}
      </List>
    </Box>
        <ViewOpeion/>
</div>
        
      
  );
}